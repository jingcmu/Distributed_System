package Lab0;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class Receiver extends Thread{
	
	public Socket socket;
	
	public Receiver(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		DataInputStream in;
		try {
			in = new DataInputStream(socket.getInputStream());
			byte[] bytes = new byte[4000];
			int n;
			while ((n = in.read(bytes)) > 0) {
				//System.out.println(n);
				Message message = Message.getMessage(bytes);
		        System.out.println("message received from " + message.src + ": " + message.data + " " + message.seq);
		        if (MessagePasser.nodes.get(message.src).socket == null) {
		        	MessagePasser.nodes.get(message.src).socket = this.socket;
		        }
		        boolean flag = true;
				for (int i = 0; i < ConfigHandler.receiveRules.size(); i++) {
					Rule rule = ConfigHandler.receiveRules.get(i);
					if (rule.match(message)) {
						if (rule.action == Action.DELAY) {
							System.out.println("DELAY match!");
							MessagePasser.receiveDelayQueue.offer(message);
						} else if (rule.action == Action.DUPLICATE) {
							System.out.println("DUPLICATE match!");
							Message duplicateMessage = new Message(message);
							duplicateMessage.dupe = true;
							MessagePasser.deliverQueue.put(message);
							MessagePasser.deliverQueue.put(duplicateMessage);
						}
						flag = false;
						break;
					}
				}
				if (flag) {
					MessagePasser.deliverQueue.put(message);
				}
			}
		} catch (IOException e) {
			System.out.println("The sender is down");
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
		} catch (InterruptedException e) {
			
		}
		
	}

}
