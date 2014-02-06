import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class SocketListener extends Thread {
	private Node node;

	public SocketListener(Node node) {
		super();
		this.node = node;
	}

	public void run() {
		try {
			ServerSocket listener = new ServerSocket(this.node.getPort());
			while (true) {
				Socket socket = listener.accept();
				MessageHandler msgHandler = new MessageHandler(socket);
				msgHandler.start();
			}
		} catch (Exception e) {
			System.out.println("Failed to set up Node \"" + this.node.getName() + "\".");
		}
	}
}
