import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class LoggerReceiver extends Thread {

	MessagePasser mp;
	ArrayList<TimestampedMessage> receivedMessages;
	Timestamp tsm = null;
	

	public LoggerReceiver(MessagePasser mp,
			ArrayList<TimestampedMessage> receivedMessages) {
		super();
		this.mp = mp;
		this.receivedMessages = receivedMessages;
	}
	
	public void run() {
		Message m = null;
		while(true) {
			m = null;
			
			while (m == null) {
				m = this.mp.receive();
			}
			
			System.out.println("\tReceived message : " + m.toString());
			synchronized (receivedMessages) {
				this.receivedMessages.add((TimestampedMessage)m);
			}
		}
	}
}
