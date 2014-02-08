package Lab0;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

public class ListenerThread implements Runnable {
    private static Logger logger = Logger.getLogger(ListenerThread.class);
    private String configFile;
    private int port;
    private ArrayList<RuleBean> recvRules;
    private ArrayList<RuleBean> sendRules;
    private LinkedBlockingDeque<Message> recvQueue;
    private LinkedBlockingDeque<Message> recvDelayQueue;
    private ServerSocket listenSocket;
    private Thread thread;

    public ListenerThread(int port, String configFile,
                            ArrayList<RuleBean> recvRules, ArrayList<RuleBean> sendRules,
                            LinkedBlockingDeque<Message> recvQueue,
                            LinkedBlockingDeque<Message> recvDelayQueue) {
        this.port = port;
        this.recvQueue = recvQueue;
        this.recvDelayQueue = recvDelayQueue;
        this.recvRules = recvRules;
        this.sendRules = sendRules;
        this.configFile = configFile;
    }

    @Override
    public void run() {
        try {
            listenSocket = new ServerSocket(this.port);
            while(true) {
                // Listening for new incoming connection.
                Socket socket = listenSocket.accept();
                logger.info("Handling client at " + socket.getRemoteSocketAddress());

                // Create a new thread for new incoming connection.
                thread = new Thread(new ReceiverThread(socket, configFile, recvRules, sendRules, recvQueue, recvDelayQueue));
                thread.start();
            }
        }
        catch (IOException e) {
            logger.error("ERROR: Server socket error");
            System.err.println("ERROR: server Socket error");
        }
    }

    public void teminate() throws IOException {
        thread.interrupt();
        listenSocket.close();
    }

    @Override
    public String toString() {
        return "Listener [port=" + port + "]";
    }
}
