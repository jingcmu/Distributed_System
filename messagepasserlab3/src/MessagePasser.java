
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;


public class MessagePasser {

	String source;
	String ip;
	int port;
	Long sequenceNumber = 0L;
	ClockService appClock = null;
	
	int nMutexMsgSent = 0;
	int nMutexMsgReceived = 0;

	private String configFilePath;
	private long lastModified = 0; /* last modified time of config file */

	ArrayList<PeerProcess> peers = new ArrayList<PeerProcess>();

	ArrayList<Rule> sendRules = new ArrayList<Rule>();
	ArrayList<Rule> receiveRules = new ArrayList<Rule>();

	ArrayList<Message> delayedMessageSendQueue = new ArrayList<Message>();

	ArrayList<Message> receivedMessagesQueue = new ArrayList<Message>();
	ArrayList<Message> delayedMessageReceiveQueue = new ArrayList<Message>();

	private long peerConnectionRetryInterval = 10000;
	private MulticastService ms = null;
	private MutualExclusionService mutexService = null;

	public MessagePasser(String configFilePath, String processName, ClockService clock) throws Exception {
	
		super();
	
		try {
			this.source = processName;
			this.configFilePath = configFilePath;
			this.appClock = clock;
			parseConfiguration(configFilePath, true);
			initProcess();
	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public boolean send(Message msg) {
		try {
			// check for compatibility of the message with sendRules and get
			// action
			
			Message m = msg.clone();
			
			m.setSource(source);
			m.setSequenceNumber(getNextSeqNum());
			
			incrementSentCount(m);
			
			Timestamp ts = appClock.getTimestamp();
			((TimestampedMessage)m).setTimestamp(ts);
	
			String action = matchRules(m, sendRules);
	
			// perform the action on Message
			if (action == null) {
				return sendMessageToPeer(m);
	
			} else if (action.equals("drop")) {
				Client.getMessageShower().setJTextStatus("Rule matched : Action - Drop the message");
				/*
				 * required to flush non-delayed messages when drop message
				 * arrives. ASSUMPTION : Drop message is also a non-delayed
				 * message.
				 */
				sendMessageToPeer(null);
				/*
				 * POLICY DECISION : Since message was not sent to destination,
				 * return false. Client can check logs to see why message was
				 * not sent!
				 */
				return false;
			} else if (action.equals("delay")) {
				/* Add message to delayQueue */
				Client.getMessageShower().setJTextStatus("Rule matched : Action - Delay\n");
				delayedMessageSendQueue.add(m);
				return true;
			} else if (action.equals("duplicate")) {
				/*
				 * duplicate the message and set the duplicate field to True in
				 * message2
				 */
				Client.getMessageShower().setJTextStatus("Rule matched : Action - Duplicate\n");
	
				Message mDup = m.clone();
				mDup.setDuplicateFlag("true");
	
				/*
				 * POLICY DECISION - return true only if both messages sent
				 * successfully
				 */
	
				boolean ret = sendMessageToPeer(m);
				if (ret == false) {
					return ret;
				}
				return sendMessageToPeer(mDup);
	
			} else {
				Client.getMessageShower().setJTextStatus("Unknown action given by Rule! ");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	

	public Message receive() {
		Message msg = null;
	
		if (receivedMessagesQueue.size() > 0) {
			// TODO
			msg =  receivedMessagesQueue.remove(0);
			return msg;
		}
		return null;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}
	
	public String getSource() {
		return source;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public ArrayList<PeerProcess> getPeers() {
		return peers;
	}

	private PeerProcess findPeer(String destination) {
			try {
				for (PeerProcess peer : peers) {
					if (destination.equals(peer.getProcessName())) {
						return peer;
					}
				}
				
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	synchronized private long getNextSeqNum() {
		sequenceNumber++;
		return sequenceNumber;
	}

	/*
	 * Initialize peers, sendRules and receiveRules if flag = true, initial
	 * setup else, check for rule updates only
	 */
	private void parseConfiguration(String FilePath, boolean flag)
			throws Exception {

		try {
			Client.getMessageShower().setJTextStatus("Parsing the config file...\n");
			File file = new File(FilePath);
			InputStream input = new FileInputStream(file);

			Yaml yaml = new Yaml();
			Map<String, Object> data = (Map<String, Object>) yaml.load(input);
			ArrayList<Map> mapElements = null;
			if (flag == true) {

				mapElements = (ArrayList<Map>) data.get("configuration");
				createPeers(mapElements);
			}

			mapElements = (ArrayList<Map>) data.get("sendRules");
			createSendRules(mapElements);

			mapElements = (ArrayList<Map>) data.get("receiveRules");
			createReceiveRules(mapElements);

			this.lastModified = file.lastModified();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private Rule createRule(Map element, StringBuffer str) throws Exception {
		try {
			String type = null;
			String source = null;
			String destination = null;
			Long sequenceNumber = null;
			String duplicate = null;

			String action = element.get("action").toString();
			if (element.get("kind") != null) {
				type = element.get("kind").toString();
			}
			if (element.get("src") != null) {
				source = element.get("src").toString();
			}
			if (element.get("dest") != null) {
				destination = element.get("dest").toString();
			}
			if (element.get("seqNum") != null) {
				sequenceNumber = Long.parseLong(element.get("seqNum")
						.toString());
			}
			if (element.get("duplicate") != null) {
				duplicate = element.get("duplicate").toString();
			}
			str.append(action + "\t" + source + "\t" + destination + "\t"
					+ type + "\t" + sequenceNumber + "\t\t" + duplicate + "\n");
			
			Rule rule = new Rule(type, action, source, destination,
					sequenceNumber, duplicate);
			return rule;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void createSendRules(ArrayList<Map> mapElements) throws Exception {
		try {

			flushSendRules();
			StringBuffer str = new StringBuffer();
			str.append("action" + "\t" + "source" + "\t" + "destination" + "\t"
					+ "type" + "\t" + "sequenceNumber" + "\t" + "duplicate" + "\n");
			for (Map element : mapElements) {
				Rule rule = createRule(element, str);
				addSendRule(rule);
			}
			Client.getMessageShower().setJTextSendRule(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createReceiveRules(ArrayList<Map> mapElements)
			throws Exception {
		try {
			flushReceiveRules();
			StringBuffer str = new StringBuffer();
			str.append("action" + "\t" + "source" + "\t" + "destination" + "\t"
					+ "type" + "\t" + "sequenceNumber" + "\t" + "duplicate" + "\n");
			for (Map element : mapElements) {
				Rule rule = createRule(element, str);
				addReceiveRule(rule);
			}
			Client.getMessageShower().setJTextRecvRule(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	private void createPeers(ArrayList<Map> mapElements) throws Exception {
		try {
			/* Display nodes in config file */
			StringBuffer str = new StringBuffer("Name    " + "   IP   " +
												"             Port" + "\n");
			for (Map element : mapElements) {
				String name, ip;
				int port;

				name = element.get("name").toString();
				ip = element.get("ip").toString();
				port = (Integer) element.get("port");

				str.append(name + "  " + ip + "  " + port + "\n");

				PeerProcess peer = new PeerProcess(name, ip, port);
				registerPeer(peer);
			}
			Client.getMessageShower().setJTextNodeConfig(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void registerPeer(PeerProcess peer) {
		this.peers.add(peer);
	}

	private void addSendRule(Rule rule) {
		this.sendRules.add(rule);
	}

	private void flushSendRules() {
		this.sendRules.clear();
	}

	private void addReceiveRule(Rule rule) {
		this.receiveRules.add(rule);
	}

	private void flushReceiveRules() {
		this.receiveRules.clear();
	}

	private void initProcess() throws Exception {
		int i;

		/*
		 * find config of this process from peers start listening at ip and port
		 * specified in config file.
		 */

		/* loop to find current process node */
		for (i = 0; i < peers.size(); i++) {
			PeerProcess peer = peers.get(i);
			if (this.source.equals(peer.getProcessName())) {
				this.ip = peer.getIp();
				this.port = peer.getPort();
				break;
			}
		}

		if (i < peers.size()) {
			// Config of local process found.
			peers.remove(i);
		} else {
			Client.getMessageShower().setJTextStatus("Config of local process " + this.source
					+ " not found.\n");
			throw new Exception("Config for local process " + this.source
					+ " not found!");
		}

		StringBuffer str = new StringBuffer();
		/* Display nodes in config file */
		for (PeerProcess peer : peers) {
			str.append(peer.getProcessName() + "\n");
		}
		Client.getMessageShower().setJTextPeer(str.toString());

		/* Start thread for listening to peer connection requests */
		startPeerConnectionAcceptor();

		/*
		 * Start thread that attempts to connect to peers recursively, with whom
		 * connection is not already open
		 */
		initPeerConnections();
	}

	private void startPeerConnectionAcceptor() throws Exception {
		/*
		 * create thread that keeps on accepting new connections. new connection
		 * updates peer.socket and start listening on that socket (one listener
		 * per peer socket)
		 */
		try {
			Thread t = new PeerAcceptor(this);
			t.start();
		} catch (IOException io) {
			io.printStackTrace();
			throw new Exception("Unable to create local socket.");
		}
	}

	private void initPeerConnections() throws Exception {
		/*
		 * create thread that keeps attempting to establish connections with
		 * peers with whom connection has not already been established.
		 * 
		 * Parameter interval is seconds before retry.
		 * 
		 * on success, it update peers.socket and start listening for messages
		 * from that peer on the newly created socket (one listener per peer
		 * socket)
		 */
		try {
			Thread t = new PeerConnector(this, peerConnectionRetryInterval);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to start thread to connect to peers.");
		}
	}

	/*
	 * synchronize across threads : accessed by 1. PeerConnector and 2. Client
	 * thread trying to send message when peer.socket is null
	 */
	protected synchronized void establishConnection(PeerProcess peer) {
		/* creates new socket from which messages can be sent to particular
		 * peer.
		 */
		try {

			Socket client = new Socket(peer.getIp(), peer.getPort());
			if (client != null) {
				Client.getMessageShower().setJTextStatus("\n[CONNECTED TO : "
						+ peer.getProcessName() + "]\n");
				peer.setSocket(client);
			}
			return;
		} catch (IOException e) {
			/* Couldn't connect to peer! */
			return;
		}
	}

	private boolean sendMessage(Message m) {
		String destination = m.getDestination();
		PeerProcess peer = findPeer(destination);

		if (destination.equals(source)) {
			this.receiveHandler(m);
			return true;
		}
		
		if (peer == null) {
			Client.getMessageShower().setJLabelStatus("Unknown destination!");
			Client.getMessageShower().setJTextStatus("Unknown destination! Message sending failed.\n");
			return false;
		}

		/* Get socket for communicating to destination */
		Socket socket = peer.getSocket();
		if (socket == null) {
			/* Try to establish connection once again! */

			this.establishConnection(peer);

			if (peer.getSocket() == null) {
				Client.getMessageShower().setJLabelStatus("Message sending failed!");
				Client.getMessageShower().setJTextStatus("\tCannot reach "+ peer.getProcessName() +"! Message sending failed.\n");
				return false;
			}
			socket = peer.getSocket();
		}

		boolean ret = sendToSocket(socket, m);
		if (ret == false) {
			/* Clear socket peer association */
			peer.setSocket(null);
		}
		return ret;
	}

	private boolean sendToSocket(Socket socket, Message m) {

		try {

			OutputStream outToServer;

			outToServer = socket.getOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(outToServer);
			output.writeObject(m);

			return true;
		} catch (IOException e) {
			Client.getMessageShower().setJLabelStatus("Message sending failed!");
			Client.getMessageShower().setJTextStatus("Error writing to socket! Message sending failed.\n");
			return false;
		}
	}
	private boolean sendMessageToPeer(Message m) {
		try {
			/*
			 * Flush delayedMessageSendQueue first and then send current
			 * message.
			 */
			boolean ret = true;
			
			Message msg = null;

			if (m != null) {
				ret = sendMessage(m);
			}
			
			while (delayedMessageSendQueue.size() > 0) {
				msg = delayedMessageSendQueue.remove(0);
				if (msg != null)
					sendMessage(msg);
			}

			return ret;

			/*
			 * Message was dropped. But that is a rule, not a failure to send
			 * message, this returning true anyway
			 */
		} catch (Exception e) {
			return false;
		}
	}

	/* called from MessageListener threads */
	protected void receiveHandler(Message m) {
		try {
			// check for match with receiveRules and get action
			//m.print();
			String action = matchRules(m, receiveRules);

			// perform the action on Message
			if (action == null) {

				dispatchReceivedMessage(m);
				return;
			} else if (action.equals("drop")) {
				Client.getMessageShower().setJLabelStatus("Action - Message droped!");
				Client.getMessageShower().setJTextStatus(
						"(Receiver handler) Rule matched : Action - Drop the message");
				/*
				 * required to flush non-delayed messages when drop message
				 * arrives. ASSUMPTION : Drop message is also a non-delayed
				 * message.
				 */
				dispatchReceivedMessage(null);
				return;
			} else if (action.equals("delay")) {
				/* Add message to delayQueue */
				Client.getMessageShower().setJLabelStatus("Action - Message delay!");
				Client.getMessageShower().setJTextStatus(
						"(Receiver handler) Rule matched : Action - delay the message");
				addToDelayedMessageReceiveQueue(m);
				return;
			} else if (action.equals("duplicate")) {
				/*
				 * duplicate the message and set the duplicate field to True in
				 * message2
				 */
				Client.getMessageShower().setJLabelStatus("Action - Message duplicate!");
				Client.getMessageShower().setJTextStatus(
						"(Receiver handler) Rule matched : Action - Duplicate");
				
				Message mDup = m.clone();
				mDup.setDuplicateFlag("true");
				dispatchReceivedMessage(m);
				dispatchReceivedMessage(mDup);
				return;
			} else {
				Client.getMessageShower().setJLabelStatus("Action - Unknown!");
				Client.getMessageShower().setJTextStatus(
						"(Receiver handler) Unknown action given by Rule! ");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private synchronized void addToDelayedMessageReceiveQueue(Message m) {
		delayedMessageReceiveQueue.add(m);
	}
	
	protected synchronized void deliverToClient(Message msg) {
		
		if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_REQUEST.toString())) {
			// This is a special multicast Message to be sent to mutexService.
			
			incrementReceivedCount(msg);
			System.out.println("Received MUTEX_REQUEST! (+) Received count = " + nMutexMsgReceived);
		
			mutexService.requestHandler((MulticastMessage)msg);
			
		}
		else if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_RELEASE.toString())) {
			// This is a special multicast Message to be sent to mutexService.
			incrementReceivedCount(msg);
			System.out.println("Received MUTEX_RELEASE! (+) Received count = " + nMutexMsgReceived);
			
			mutexService.releaseHandler((MulticastMessage)msg);
			
		}
		else if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_ACK.toString())) {
			incrementReceivedCount(msg);
			System.out.println("Received MUTEX_ACK! (+) Received count = " + nMutexMsgReceived);
			
			mutexService.replyHandler(msg);
			
		}
		else {
			// This is a normal message to be delivered to Client
			addtoReceivedMessagesQueue(msg);
			//msg.print();
		}
		
	}
	
	protected synchronized void addtoReceivedMessagesQueue(Message m) {
		receivedMessagesQueue.add(m);
	}
	
	private synchronized void __dispatchReceivedMessage(Message m) {
		appClock.updateTimestamp(((TimestampedMessage) m).getTimestamp());
		
		if (MulticastMessage.class.isInstance(m)) {
			ms.bDeliver((MulticastMessage)m);
			return;
		}
		deliverToClient(m);
	}
	
	private synchronized void dispatchReceivedMessage(Message m) {

		Message msg = null;
		
		
		if (m != null) {
			__dispatchReceivedMessage(m);
		}
		
		while (delayedMessageReceiveQueue.size() > 0) {
			msg = delayedMessageReceiveQueue.remove(0);
			__dispatchReceivedMessage(msg);
			
		}
		
	}
	/*
	private synchronized void addToReceiveMessageQueue(Message m) {
		
	}
	*/

	private String matchRules(Message m, ArrayList<Rule> rules) throws Exception {
		try {

			checkConfigUpdates();

			for (int i = 0; i < rules.size(); i++) {

				Rule rule = rules.get(i);

				String source = rule.getSource();
				if (source != null && !source.equals(m.getSource())) {
					continue;
				}

				String destination = rule.getDestination();
				if (destination != null
						&& !destination.equals(m.getDestination())) {
					continue;
				}

				String kind = rule.getType();
				if (kind != null && !kind.equals(m.getKind())) {
					continue;
				}

				String duplicate = rule.getDuplicate();
				if (duplicate != null
						&& !duplicate.equals(m.getDuplicateFlag())) {
					continue;
				}

				Long sequenceNumber = rule.getSequenceNumber();
				if (sequenceNumber != null
						&& (long)sequenceNumber != (long)m.getSequenceNumber()) {
					continue;
				}

				Client.getMessageShower().setJTextStatus("Message passes criteria of Rule[" + (i + 1)+ "]\n");
				return rule.getAction();
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private synchronized void checkConfigUpdates() {
		try {
			// check for modification time
			File file = new File(this.configFilePath);

			// if file modified then only refresh rules.
			if (this.lastModified < file.lastModified()) {
				Client.getMessageShower().setJTextStatus("\nConfig file modified! Refreshing Rules.\n");
				/* parse modified file, to refresh Rules. <flag = false> */
				parseConfiguration(this.configFilePath, false);
				Client.getMessageShower().setJTextStatus("\n");
				// update previousModified to new value
				this.lastModified = file.lastModified();
			}
		} catch (Exception e) {
			Client.getMessageShower().setJLabelStatus("Config Error!");
			Client.getMessageShower().setJTextStatus("Error updating config.\n");
		}
	}

	public void setMulticastService(MulticastService ms) {
		this.ms  = ms;
	}

	public void setMutexService(MutualExclusionService mutexService) {
		this.mutexService = mutexService;
		
	}
	
	private synchronized void incrementSentCount(Message msg) {
		if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_REQUEST.toString())) {
			// This is a special multicast request which is flooded.
			// only count if origin == this.processname
			if (((MulticastMessage)msg).getOrigin().equals(source)) {
				nMutexMsgSent++;
				System.out.println("Sending MUTEX_REQUEST! (+) Sent count = " + nMutexMsgSent);
			}
		}
		else if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_RELEASE.toString())) {
			// This is a release message, which is a multicast message.
			// only count if origin = this.processname
			if (((MulticastMessage)msg).getOrigin().equals(source)) {
				nMutexMsgSent++;
				System.out.println("Sending MUTEX_RELEASE! (+) Sent count = " + nMutexMsgSent);
			}
		}
		else if (msg.getKind().equals(MutualExclusionService.MessageType.MUTEX_ACK.toString())) {
			// this is a unicast ack message being sent. count this.
			nMutexMsgSent++;
			System.out.println("Sending MUTEX_ACK! (+) Sent count = " + nMutexMsgSent);
		}
		else {
			return;
		}
		
		
	}
	
	private synchronized void incrementReceivedCount(Message m) {
		nMutexMsgReceived++;
	}
	
	public synchronized int getnMutexMsgSent() {
		return nMutexMsgSent;
	}

	public synchronized int getnMutexMsgReceived() {
		return nMutexMsgReceived;
	}
	
}
