package Lab0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import distSysLab0.RuleBean.RuleAction;

public class MessagePasser {
    private static MessagePasser instance;
    private static Logger logger = Logger.getLogger(MessagePasser.class);

    private LinkedBlockingDeque<Message> sendQueue = new LinkedBlockingDeque<Message>();
    private LinkedBlockingDeque<Message> sendDelayQueue = new LinkedBlockingDeque<Message>();
    private LinkedBlockingDeque<Message> recvQueue = new LinkedBlockingDeque<Message>();
    private LinkedBlockingDeque<Message> recvDelayQueue = new LinkedBlockingDeque<Message>();
    private HashMap<String, NodeBean> nodeList = new HashMap<String, NodeBean>();
    private ArrayList<RuleBean> sendRules = new ArrayList<RuleBean>();
    private ArrayList<RuleBean> recvRules = new ArrayList<RuleBean>();

    private String configFile;
    private String localName;
    private String MD5Last;
    private int curSeqNum;

    private ListenerThread listener;
    private SenderThread sender;

    /**
     * Actual constructor for MessagePasser
     *
     * @param configFile
     * @param localName
     */
    private MessagePasser(String configFile, String localName)
            throws UnknownHostException {
        this.localName = localName;
        this.configFile = configFile;
        this.curSeqNum = 0;

        ConfigParser.configurationFile = configFile;
        nodeList = ConfigParser.readConfig();
        sendRules = ConfigParser.readSendRules();
        recvRules = ConfigParser.readRecvRules();
        MD5Last = ConfigParser.getMD5Checksum(configFile);

        if(nodeList.get(localName) == null) {
            logger.error("The local name is incorrect.");
            System.exit(0);
        }
        else if (!InetAddress.getLocalHost().getHostAddress().toString().equals(nodeList.get(localName).getIp())) {
            logger.error("Local ip do not match configuration file.");
            System.exit(0);
        }
        else {
            listener = new ListenerThread(nodeList.get(localName).getPort(), configFile,
                                            recvRules, sendRules, recvQueue, recvDelayQueue);
            sender = new SenderThread(sendQueue, sendDelayQueue, nodeList);
        }

        logger.debug(this.toString());
    }

    /**
     * Initialization for receive thread.
     */
    public synchronized void startListener() {
        Thread thread = new Thread(this.listener);
        thread.start();
    }

    /**
     * Initialization for send thread.
     */
    public synchronized void startSender() {
        Thread thread = new Thread(this.sender);
        thread.start();
    }

    /**
     * Singleton constructor for MessagePasser
     *
     * @param configuration_filename
     * @param local_name
     */
    public static synchronized MessagePasser getInstance(String configuration_filename, String local_name)
                                                                 throws UnknownHostException {
        if (instance == null) {
            instance = new MessagePasser(configuration_filename, local_name);
        }
        return instance;
    }

    /**
     * Return existed instance of MessagePasser
     *
     * @return instance
     */
    public static MessagePasser getInstance() {
        return instance;
    }

    /**
     * Send a message.
     *
     * @param message The message need to be sent.
     */
    public void send(Message message) {
        // Set source and seq of the massage
        message.setSrc(localName);
        message.setSeqNum(curSeqNum++);

        // Check if the configuration file has been changed.
        String MD5 = ConfigParser.getMD5Checksum(configFile);
        if (!MD5.equals(MD5Last)) {
            sendRules = ConfigParser.readSendRules();
            recvRules = ConfigParser.readRecvRules();
            MD5Last = MD5;
        }

        // Try to match a rule from the send rule list.
        RuleAction action = RuleAction.NONE;
        for (RuleBean rule : sendRules) {
            if (rule.isMatch(message)) {
                action = rule.getAction();
            }
        }

        // Do action according to the matched rule's type.
        switch (action) {
        case DROP:
            // Just drop this message.
            break;

        case DUPLICATE:
            // Add this message into sendQueue.
            sendQueue.add(message);
            // Add a duplicate message into sendQueue.
            Message copy = message.copyOf();
            copy.setDuplicate(true);
            sendQueue.add(copy);
            sendQueue.addAll(sendDelayQueue);
            sendDelayQueue.clear();
            break;

        case DELAY:
            // Add this message into delayQueue
            sendDelayQueue.add(message);
            break;

        case NONE:
        default:
            // Add this message into sendQueue
            sendQueue.add(message);
            sendQueue.addAll(sendDelayQueue);
            sendDelayQueue.clear();
        }
    }

    /**
     * Deliver message from the receive queue
     *
     * @return A message
     */
    public Message receive() {
        Message message = null;
        synchronized (recvQueue) {
            if (!recvQueue.isEmpty()) {
                message = recvQueue.poll();
            }
        }
        return message;
    }

    /**
     * Do the termination work.
     */
    public void teminate() throws IOException {
        listener.teminate();

        sender.teminate();
    }

    public HashMap<String, NodeBean> getNodeList() {
        return nodeList;
    }

    @Override
    public String toString() {
        return "MessagePasser [configFile=" + configFile + ", localName=" + localName
                + ", listenSocket=" + listener.toString() + "]";
    }
}