import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class MessagePasser {
	private static MessagePasser msgPasser;	// Singleton pattern
	private String configFileName;
	private String localName;
	private Configuration config;
	private Lock configLock;
	private long configLastModified;
	private Node localNode;
	private int seqNum;
	private Queue<Message> sendDelayBuf;
	private Lock sendBufLock;
	private Queue<Message> recvDelayBuf;
	private Queue<Message> recvBuf;
	private Lock recvBufLock;
	private Map<String, Socket> node2socket;
	private Lock node2socketLock;

	public MessagePasser(String configFileName, String localName) {
		super();

		this.configFileName = configFileName;
		this.localName = localName;
		this.configLock = new ReentrantLock();
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
		this.sendDelayBuf = new LinkedList<Message>();
		this.sendBufLock = new ReentrantLock();
		this.recvDelayBuf = new LinkedList<Message>();
		this.recvBuf = new LinkedList<Message>();
		this.recvBufLock = new ReentrantLock();
		this.node2socket = new HashMap<String, Socket>();
		this.node2socketLock = new ReentrantLock();
		
		SocketListener socketListener = new SocketListener(this.localNode);
		socketListener.start();
	}

	private void parseConfigFile(String fileName) {
		try {
			File file = new File(fileName);
			this.configLastModified = file.lastModified();
			InputStream is = new FileInputStream(file);
			Yaml yaml = new Yaml(new Constructor(Configuration.class));
			this.configLock.lock();
			try {
				this.config = (Configuration) yaml.load(is);
			} finally {
				this.configLock.unlock();
			}
		} catch (Exception e) {
			System.out.println("Failed to parse configuration file.");
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

	public String send(Message msg) {
		msg.setSrc(this.localName);
		msg.setSeqNum(this.seqNum++);
		ClockService clockService = ClockService.getInstance(); //could be used in the whole func
		if(msg instanceof TimeStampedMessage) {			
			if(clockService == null) {
				System.out.println("Failed to get ClockService.");
				System.exit(1);
			}
			//((TimeStampedMessage)msg).setTimeStamp(clockService.getTimeStamp()); //not to set time stamp here
		}
		String action = this.matchSendRule(msg);
		if (action == null) {
			this.sendMsg(msg);
			this.sendBufLock.lock();
			try {
				this.clearSendDelayBuf(clockService);
			} finally {
				this.sendBufLock.unlock();
			}
		} else {
			if (action.equals(Constants.actionDrop)) {
				return action;
			} else if (action.equals(Constants.actionDuplicate)) {
				((TimeStampedMessage)msg).setTimeStamp(clockService.getTimeStamp()); //set time stamp
				this.sendMsg(msg);
				this.sendBufLock.lock();
				try {
					this.clearSendDelayBuf(clockService);
				} finally {
					this.sendBufLock.unlock();
				}
				if(msg instanceof TimeStampedMessage) {
					TimeStampedMessage dupeMsg = new TimeStampedMessage((TimeStampedMessage)msg);
					dupeMsg.setDupe(true);
					((TimeStampedMessage)dupeMsg).setTimeStamp(clockService.getTimeStamp()); //set time stamp again for dupeMsg
					this.sendMsg(dupeMsg);
				} else {
					Message dupeMsg = new Message(msg);
					dupeMsg.setDupe(true);
					this.sendMsg(dupeMsg);
				}
			} else if (action.equals(Constants.actionDelay)) {
				this.sendBufLock.lock();
				try {
					this.sendDelayBuf.add(msg);
				} finally {
					this.sendBufLock.unlock();
				}
			}
		}
		return action;
	}

	public Message receive() {
		ClockService clockService = ClockService.getInstance();
		if(clockService == null) {
			System.out.println("Failed to get ClockService.");
			System.exit(1);
		}
		if(!this.recvBuf.isEmpty()) {
			Message msg = this.recvBuf.remove();
			if(msg instanceof TimeStampedMessage) {
				clockService.updateTimeStamp(((TimeStampedMessage)msg).getTimeStamp());
			} else {
				clockService.updateTimeStamp(null);
			}
			return msg;
		} else {
			clockService.updateTimeStamp(null);
			return null;
		}
	}

	/* 
	 * Send the message to the destination specified inside the message
	 */
	private void sendMsg(Message msg) {
		String dest = msg.getDest();
		Socket socket = null;
		this.node2socketLock.lock();
		try {
			if (this.node2socket.containsKey(dest)) {
				socket = this.node2socket.get(dest);
			} else {
				this.configLock.lock();
				try {
					for (Node n : this.config.getConfiguration()) {
						if (n.getName().equals(dest)) {
							socket = new Socket(n.getIp(), n.getPort());
							break;
						}
					}
				} finally {
					this.configLock.unlock();
				}
				MessageHandler msgHandler = new MessageHandler(socket);
				msgHandler.start();
				this.addSocketToMap(dest, socket);
			}
		} catch (Exception e) {
		} finally {
			this.node2socketLock.unlock();
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

	private void clearSendDelayBuf(ClockService clockService) {
		while (!this.sendDelayBuf.isEmpty()) {
			Message msg = this.sendDelayBuf.remove();
			if(msg instanceof TimeStampedMessage) {
				((TimeStampedMessage)msg).setTimeStamp(clockService.getTimeStamp()); //set time stamp when really sending the message
			}
			this.sendMsg(msg);
		}
	}

	/*
	 * Add an entry <nodeName, socket> into the map
	 */
	public void addSocketToMap(String nodeName, Socket socket) {
		this.node2socketLock.lock();
		try {
			if (!this.node2socket.containsKey(nodeName)) {
				this.node2socket.put(nodeName, socket);
			}
		} finally {
			this.node2socketLock.unlock();
		}
	}

	/* 
	 * Remove the entry with key <src> from the map
	 */
	public void removeSocketFromMap(String src) {
		this.node2socketLock.lock();
		try {
			this.node2socket.remove(src);
		} finally {
			this.node2socketLock.unlock();
		}
	}

	public void addMsgToBuf(Message msg) {
		String action = this.matchRecvRule(msg);
		if (action == null) {
			this.recvBufLock.lock();
			try {
				this.recvBuf.add(msg);
				this.clearRecvDelayBuf();
			} finally {
				this.recvBufLock.unlock();
			}
		} else {
			if (action.equals(Constants.actionDrop)) {
				return;
			} else if (action.equals(Constants.actionDuplicate)) {
				this.recvBufLock.lock();
				try {
					Message dupeMsg = new Message(msg);
					dupeMsg.setDupe(true);
					this.recvBuf.add(msg);
					this.clearRecvDelayBuf();
					this.recvBuf.add(dupeMsg);
				} finally {
					this.recvBufLock.unlock();
				}
			} else if (action.equals(Constants.actionDelay)) {
				this.recvBufLock.lock();
				try {
					this.recvDelayBuf.add(msg);
				} finally {
					this.recvBufLock.unlock();
				}
			}
		}
	}

	/*
	 * Match msg against the send rule
	 */
	private String matchSendRule(Message msg) {
		this.checkConfigFile();
		this.configLock.lock();
		try {
			for (Rule r : this.config.getSendRules()) {
				if (equalsIfNotNull(msg.getSrc(), r.getSrc())
						&& equalsIfNotNull(msg.getDest(), r.getDest())
						&& equalsIfNotNull(msg.getKind(), r.getKind())
						&& equalsIfNotNull(msg.getSeqNum(), r.getSeqNum())
						&& equalsIfNotNull(msg.isDupe(), r.isDuplicate())) {
					return r.getAction();
				}
			}
		} finally {
			this.configLock.unlock();
		}
		return null;
	}

	/*
	 * Match msg agianst the receive rule
	 */
	private String matchRecvRule(Message msg) {
		this.checkConfigFile();
		this.configLock.lock();
		try {
			for (Rule r : this.config.getReceiveRules()) {
				if (equalsIfNotNull(msg.getSrc(), r.getSrc())
						&& equalsIfNotNull(msg.getDest(), r.getDest())
						&& equalsIfNotNull(msg.getKind(), r.getKind())
						&& equalsIfNotNull(msg.getSeqNum(), r.getSeqNum())
						&& equalsIfNotNull(msg.isDupe(), r.isDuplicate())) {
					return r.getAction();
				}
			}
		} finally {
			this.configLock.unlock();
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

	private void clearRecvDelayBuf() {
		while (!this.recvDelayBuf.isEmpty()) {
			this.recvBuf.add(this.recvDelayBuf.remove());
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
		this.configLock.lock();
		try {
			for (Node n : this.config.getConfiguration()) {
				if (!n.getName().equals(this.localName)) {
					result.add(n);
				}
			}
		} finally {
			this.configLock.unlock();
		}
		return result;
	}
	
	public void add2sendDelayBuf(Message e) {
		this.sendDelayBuf.add(e);
	}
}


