package Lab0;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import distSysLab0.RuleBean.RuleAction;

public class ReceiverThread implements Runnable {
    private static Logger logger = Logger.getLogger(ReceiverThread.class);

    private Socket socket;
    private ObjectInputStream in;
    private ArrayList<RuleBean> recvRules;
    private ArrayList<RuleBean> sendRules;
    private LinkedBlockingDeque<Message> recvQueue;
    private LinkedBlockingDeque<Message> recvDelayQueue;
    private String configFile;
    private String MD5Last;

    public ReceiverThread(Socket socket, String configFile,
                            ArrayList<RuleBean> recvRules, ArrayList<RuleBean> sendRules,
                            LinkedBlockingDeque<Message> recvQueue,
                            LinkedBlockingDeque<Message> recvDelayQueue) {
        this.socket = socket;
        this.recvQueue = recvQueue;
        this.in = null;
        this.recvDelayQueue = recvDelayQueue;
        this.recvRules = recvRules;
        this.configFile = configFile;
        this.sendRules = sendRules;
        MD5Last = "";
    }

    @Override
    public void run() {
        try {
            while(true) {
                Message message = null;
                String MD5 = ConfigParser.getMD5Checksum(configFile);
                if (!MD5.equals(MD5Last)) {
                    sendRules = ConfigParser.readSendRules();
                    recvRules = ConfigParser.readRecvRules();
                    MD5Last = MD5;
                }

                in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                if((message = (Message) (in.readObject())) != null) {
                    // Try to match a rule and act corresponding
                    // The match procedure should be in the listener thread
                    logger.info((String) message.getData());
                    RuleAction action = RuleAction.NONE;
                    for (RuleBean rule : recvRules) {
                        if (rule.isMatch(message)) {
                            action = rule.getAction();
                        }
                    }

                    synchronized(recvQueue) {
                        // Do action according to the matched rule's type.
                        // if one non-delay message comes(even with drop kind?),
                        // then all messages in delay queue go to normal queue
                        switch (action) {
                            case DROP:
                                // Just drop this message.
                                break;
                            case DUPLICATE:
                                // Add this message into recvQueue.
                                recvQueue.add(message);
                                // Add a duplicate message into recvQueue.
                                Message copy = message.copyOf();
                                copy.setDuplicate(true);
                                recvQueue.add(copy);
                                recvQueue.addAll(recvDelayQueue);
                                recvDelayQueue.clear();
                                break;
                            case DELAY:
                                // Add this message into delayQueue
                                recvDelayQueue.add(message);
                                break;
                            case NONE:
                            default:
                                recvQueue.add(message);
                                recvQueue.addAll(recvDelayQueue);
                                recvDelayQueue.clear();
                        }
                    }
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void teminate() throws IOException {
        socket.close();
    }
}
