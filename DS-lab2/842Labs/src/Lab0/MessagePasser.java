package Lab0;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Lab1.ClockFactory;
import Lab1.ClockService;
import Lab1.TimeStampedMessage;

public class MessagePasser {
	
	public ClockFactory clockFactory;
	
	public String clockType;

	public static HashMap<String, Node> nodes = new HashMap<String, Node>();

	public String name;

	public static ServerSocket serverSocket;

	public int seq;
	
	public int id;
	
	public static BlockingQueue<Message> deliverQueue = new ArrayBlockingQueue<Message>(
			1000);

	public static Queue<Message> sendDelayQueue = new LinkedList<Message>();

	public static Queue<Message> receiveDelayQueue = new LinkedList<Message>();

	public MessagePasser(String configuration_filename, String local_name, String clockType)
			throws IOException {
		ConfigChecker checker = new ConfigChecker(configuration_filename, local_name, this);
		checker.check();
		this.name = local_name;
		serverSocket = new ServerSocket(nodes.get(local_name).port);
		
		this.clockType = clockType;
		clockFactory = new ClockFactory(this);
	}

	public void send(Message message) throws IOException, InterruptedException {
		message.set_source(name);
		message.set_seqNum(++seq);
		
		if (message instanceof TimeStampedMessage) {
			TimeStampedMessage timeStampedMessage = (TimeStampedMessage)message;
			timeStampedMessage.setTimeStamp(clockFactory.getClockType().getTimeStamp());
			clockFactory.getClockType().increment();
		}
		
		boolean send = true;
		boolean check = true;

		for (int i = 0; i < ConfigHandler.sendRules.size(); i++) {
			Rule rule = ConfigHandler.sendRules.get(i);
			if (rule.match(message)) {
				if (rule.action == Action.DELAY) {
					System.out.println("DELAY match!");
					MessagePasser.sendDelayQueue.offer(message);
					check = false;
				} else if (rule.action == Action.DUPLICATE) {
					System.out.println("DUPLICATE match!");
					Message duplicateMessage = new Message(message);
					duplicateMessage.dupe = true;
					sendMessage(message);
					sendMessage(duplicateMessage);
				} else if (rule.action == Action.DROP) {
					System.out.println("DROP match!");
					check = false;
				}
				send = false;
				break;
			}
		}

		if (send) {
			sendMessage(message);
		}
		Thread.sleep(300);
		if (check) {
			while (!sendDelayQueue.isEmpty()) {
				sendMessage(sendDelayQueue.poll());
				Thread.sleep(300);
			}
		}
		
		
	}

	private void sendMessage(Message message) throws UnknownHostException,
			IOException {
		Node node = nodes.get(message.dest);
		if (node.socket == null) {
			try {
				node.socket = new Socket(node.ip, node.port);
				Listener.pool.submit(new Receiver(node.socket));
			} catch (Exception e) {
				System.out.println("The receiver " + node.name + " is not ready");
				node.socket = null;
				return;
			}
			
		}
		
		if (message instanceof TimeStampedMessage && message.log.equals("true")) {
			TimeStampedMessage timeStampedMessage = (TimeStampedMessage)message;
			TimeStampedMessage log = new TimeStampedMessage(timeStampedMessage);
			log.dest = "logger";
			log.log = "false";
			sendMessage(log);
		}
		
		OutputStream out = node.socket.getOutputStream();
		DataOutputStream out2 = new DataOutputStream(out);
		try {
			out2.write(message.toBytes());
		} catch (SocketException e) {
			System.out.println("The receiver " + node.name + " is down");
			node.socket.close();
			node.socket = null;
			return;
		}
		System.out.println("message sent from " + message.src + ": "
				+ message.data);
	}

	@SuppressWarnings("unchecked")
	public Message receive(String ifLog) throws IOException, ClassNotFoundException,
			InterruptedException {
		Message message = deliverQueue.take();
		
		if (message instanceof TimeStampedMessage) {
			TimeStampedMessage timeStampedMessage = (TimeStampedMessage)message;
			clockFactory.getClockType().clockSynchronize(timeStampedMessage);
			clockFactory.getClockType().print();
			if (ifLog.equals("true")) {
				TimeStampedMessage log = new TimeStampedMessage(timeStampedMessage);
				log.log = "false";
				log.dest = "logger";
				log.src = name;
				log.setTimeStamp(clockFactory.getClockType().getTimeStamp());
				sendMessage(log);
			}
			
		}
		
		while (!receiveDelayQueue.isEmpty()) {
			deliverQueue.put(receiveDelayQueue.poll());
		}

		System.out.println("message delivered from " + message.src + ": "
				+ message.data);

		return message;
	}

	public String getClockType() {
		return clockType;
	}

	public int getId() {
		return id;
	}

	public void increment() {
		clockFactory.getClockType().increment();
	}

	
	

	

}
