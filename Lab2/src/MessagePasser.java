import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class MessagePasser {
	private static MessagePasser msgPasser;	// Singleton pattern
	
	private String configFileName;
	private String localName;
	private Configuration config;
	private long configLastModified;
	private Node localNode;
	
	private int seqNum;
	
	private BlockingQueue<Message> sendDelayBuf;
	private BlockingQueue<Message> recvDelayBuf;
	private BlockingQueue<Message> recvBuf;
	private BlockingQueue<TimeStampedMessage> holdBackBuf;
	
	private ConcurrentHashMap<String, Socket> node2socket;

	public MessagePasser(String configFileName, String localName) {
		super();

		this.configFileName = configFileName;
		this.localName = localName;
		this.parseConfigFile(this.configFileName);
		if (this.config == null) {
			System.out.println("Failed to create MessagePasser.");
			System.exit(1);
		}
		this.localNode = this.getLocalNode(localName);
		if (this.localNode == null) {
			System.out.println("Failed to find Node \"" + localName
					+ "\" in configuration file");
			System.exit(1);
		}

		this.seqNum = 0;
		this.sendDelayBuf = new ArrayBlockingQueue<Message>(1024);
		this.recvDelayBuf = new ArrayBlockingQueue<Message>(1024);
		this.recvBuf = new ArrayBlockingQueue<Message>(1024);
		this.holdBackBuf = new ArrayBlockingQueue<TimeStampedMessage>(1024);
		
		this.node2socket = new ConcurrentHashMap<String, Socket>();
		
		SocketListener socketListener = new SocketListener(this.localNode);
		socketListener.start();
	}

	private void parseConfigFile(String fileName) {
		try {
			File file = new File(fileName);
			this.configLastModified = file.lastModified();
			InputStream is = new FileInputStream(file);
			Yaml yaml = new Yaml(new Constructor(Configuration.class));
			
			this.config = (Configuration) yaml.load(is);
			this.config.postProcess();
		} catch (Exception e) {
			//System.out.println("Failed to parse configuration file.");
			e.printStackTrace();
		}
	}

	/*
	 * Update the configuration file if updated
	 */
	private void checkConfigFile() {
		try {
			File file = new File(this.configFileName);
			if (this.configLastModified == file.lastModified()) {
				return;
			}
			this.parseConfigFile(this.configFileName);
		} catch (Exception e) {
			System.out.println("Failed to update configuration file.");
		}
	}

	/* 
	 * Get the node corresponding to localName
	 */
	private Node getLocalNode(String name) {
		for (Node n : this.config.getConfiguration()) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}
	
	public String getlocalName() {
		return this.localName;
	}
	
	public void send(Message msg) {
		msg.oriSrc = this.localName;
		msg.src = this.localName;
		msg.setSeqNum(this.seqNum++);
		
		if (msg instanceof TimeStampedMessage) {
			ClockService clockService = ClockService.getInstance();
			if (clockService == null) {
				System.out.println("Failed to get ClockService.");
				System.exit(1);
			}
			clockService.getTimeStamp().increment(); // V(g,i)[i]=V(g,i)[i]+1
			System.out.println(clockService.getTimeStamp().printTimeStamp());
			((TimeStampedMessage)msg).setTimeStamp(clockService.getTimeStamp());
			System.out.println("message timestamp: " + ((TimeStampedMessage)msg).getTimeStamp().printTimeStamp());
		}
		
		if (msg.destGroup != null) {
			bMulticast(msg);
		} else {
			sendSingleMessage(msg);
		}
		
		
	}
	
	private void sendSingleMessage(Message msg) {
		String action = this.matchSendRule(msg);
		if (action == null) {
			this.sendMsg(msg);
			this.clearSendDelayBuf();
		} else {
			if (action.equals(Constants.actionDrop)) {
				System.out.println("Drop match");
				return;
			} else if (action.equals(Constants.actionDuplicate)) {
				System.out.println("Duplicate match");
				this.sendMsg(msg);
				this.clearSendDelayBuf();
				if(msg instanceof TimeStampedMessage) {
					TimeStampedMessage dupeMsg = new TimeStampedMessage((TimeStampedMessage)msg);
					dupeMsg.setDupe(true);
					this.sendMsg(dupeMsg);
				} else {
					Message dupeMsg = new Message(msg);
					dupeMsg.setDupe(true);
					this.sendMsg(dupeMsg);
				}
			} else if (action.equals(Constants.actionDelay)) {
				System.out.println("Delay match");
				this.sendDelayBuf.offer(msg);
			}
		}
		
	}
	
	/**
	 * send to every member in the group
	 * @param message
	 */
	public void bMulticast(Message message) {
		String dest = message.destGroup;
		Group group = config.getHashroups().get(dest);
		for(Node n : group.getNodes()) {
			if(!n.getName().equals(this.localName) && !n.getName().equals(message.oriSrc)) {
				//System.out.println("b-multicast to " + n.getName());
				message.setSrc(localName);
				message.setDest(n.getName());
				sendSingleMessage(message);
			}
		}
	}
	
	/* 
	 * Send the message to the destination specified inside the message
	 */
	private void sendMsg(Message msg) {
		String dest = msg.getDest();
		Socket socket = null;
		if (this.node2socket.containsKey(dest)) {
			socket = this.node2socket.get(dest);
		} else {
			for (Node n : this.config.getConfiguration()) {
				if (n.getName().equals(dest)) {
					try {
						socket = new Socket(n.getIp(), n.getPort());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			MessageHandler msgHandler = new MessageHandler(socket);
			msgHandler.start();
			this.addSocketToMap(dest, socket);
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(msg);
			oos.flush();
		} catch (Exception e) {
			this.removeSocketFromMap(dest);
			System.out.println("Failed to send message to Node \"" + dest
					+ "\".");
		}
	}
	

	public Message receive() {
		if (!this.recvBuf.isEmpty()) {
			Message msg = this.recvBuf.poll();
			return msg;
		}
		
		return null;
	}

	public synchronized void addMsgToBuf(Message msg) {
		String action = this.matchRecvRule(msg);
		if (action == null) {
			bDeliver((TimeStampedMessage)msg);
		} else {
			if (action.equals(Constants.actionDrop)) {
				System.out.println("Drop match");
				return;
			} else if (action.equals(Constants.actionDuplicate)) {
				System.out.println("Duplicate match");
				Message dupeMsg = new Message(msg);
				dupeMsg.setDupe(true);
				bDeliver((TimeStampedMessage)msg);
				bDeliver((TimeStampedMessage)dupeMsg);
			} else if (action.equals(Constants.actionDelay)) {
				System.out.println("Delay match");
				this.recvDelayBuf.offer(msg);
			}
		}
		
	}
	
	public void bDeliver(TimeStampedMessage message) {
		System.out.println("Message received from " + message.src + " " + message.data + " " + message.seqNum);
		Node srcNode = config.getHashnode().get(message.oriSrc);
		Boolean result = srcNode.getReceived().putIfAbsent(message.seqNum, true);
		if (result != null) {
			System.out.println("received before. Return..");
			return;
		}
		/*if (srcNode.getReceived().contains(message.seqNum)) {
			System.out.println("received before. Return..");
			return;
		}
		srcNode.getReceived().add(message.seqNum);*/
		
		if (message.destGroup != null) {
			bMulticast(new TimeStampedMessage(message));
		}
		
		// add to either hold back queue or deliver queue
		Group group = config.getHashroups().get(message.destGroup);
		//System.out.println("current group timestamp: " + group.getTimestamp().printTimeStamp());
		if (group != null && !group.getTimestamp().isOrdered(message)) {
			System.out.println("not ordered: added to hold back queue");
			holdBackBuf.offer(message);
		} else {
			recvBuf.offer(message);
			
			if (message.destGroup != null) {
				group.getTimestamp().updateTimeStampMulticast(message);
				System.out.println("group timestamp after update: " + group.getTimestamp().printTimeStamp());
			} else {
				ClockService clockService = ClockService.getInstance();
				clockService.getTimeStamp().updateTimeStamp(message.getTimeStamp());
				System.out.println("current timestamp after update: " + clockService.getTimeStamp().printTimeStamp());
			}
			
			for (TimeStampedMessage holdMessage : holdBackBuf) {
				System.out.println("checking message " + holdMessage.seqNum + " in hold back queue");
				Group g = config.getHashroups().get(holdMessage.destGroup);
				if (g.getTimestamp().isOrdered(holdMessage)) {
					System.out.println("ordered: add to the deliver queue");
					recvBuf.offer(holdMessage);
				}
			}
		}
		
		
		clearRecvDelayBuf();
	}

	/*
	 * Match msg against the send rule
	 */
	private String matchSendRule(Message msg) {
		this.checkConfigFile();
		for (Rule r : this.config.getSendRules()) {
			if (equalsIfNotNull(msg.getSrc(), r.getSrc())
				&& equalsIfNotNull(msg.getDest(), r.getDest())
				&& equalsIfNotNull(msg.getKind(), r.getKind())
				&& equalsIfNotNull(msg.getSeqNum(), r.getSeqNum())
				&& equalsIfNotNull(msg.isDupe(), r.isDuplicate())) {
					return r.getAction();
				}
		}
		return null;
	}

	/*
	 * Match msg agianst the receive rule
	 */
	private String matchRecvRule(Message msg) {
		this.checkConfigFile();
		for (Rule r : this.config.getReceiveRules()) {
			if (equalsIfNotNull(msg.getSrc(), r.getSrc())
					&& equalsIfNotNull(msg.getDest(), r.getDest())
					&& equalsIfNotNull(msg.getKind(), r.getKind())
					&& equalsIfNotNull(msg.getSeqNum(), r.getSeqNum())
					&& equalsIfNotNull(msg.isDupe(), r.isDuplicate())) {
				return r.getAction();
			}
		}
		return null;
	}

	private boolean equalsIfNotNull(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null) {
			return true;
		} else {
			return obj1.equals(obj2);
		}
	}
	
	/*
	 * Add an entry <nodeName, socket> into the map
	 */
	public void addSocketToMap(String nodeName, Socket socket) {
		if (!this.node2socket.containsKey(nodeName)) {
			this.node2socket.put(nodeName, socket);
		}
	}

	/* 
	 * Remove the entry with key <src> from the map
	 */
	public void removeSocketFromMap(String src) {
		this.node2socket.remove(src);
	}
	
	private void clearSendDelayBuf() {
		while (!this.sendDelayBuf.isEmpty()) {
			this.sendMsg(this.sendDelayBuf.poll());
		}
	}

	private void clearRecvDelayBuf() {
		while (!this.recvDelayBuf.isEmpty()) {
			bDeliver((TimeStampedMessage)recvDelayBuf.poll());
		}
	}

	/*
	 * Create an instance of MessagePasser as singleton pattern
	 */
	public static void createInstance(String configFileName, String localName) {
		MessagePasser.msgPasser = new MessagePasser(configFileName, localName);
	}

	/*
	 * Get an instance of MessagePasser as singleton pattern
	 * This should be invoked after createInstance is invoked
	 */
	public static MessagePasser getInstance() {
		return MessagePasser.msgPasser;
	}

	/*
	 * Get a list of Node which does not contain the local node itself
	 */
	public List<Node> getNodeList() {
		List<Node> result = new ArrayList<Node>();
			for (Node n : this.config.getConfiguration()) {
				if (!n.getName().equals(this.localName)) {
					result.add(n);
				}
			}
		return result;
	}
	
	/*
	 * Get a list of Group 
	 */
	public List<Group> getGroupList() {
		List<Group> groups = new ArrayList<Group>();
			for (Group g : this.config.getGroups()) {
				groups.add(g);
			}
		return groups;
	}
}
