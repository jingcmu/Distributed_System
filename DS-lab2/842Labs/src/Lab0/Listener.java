package Lab0;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener extends Thread {

	public static ExecutorService pool = Executors.newFixedThreadPool(10);

	public void run() {
		while (true) {
			Socket server;
			try {
				server = MessagePasser.serverSocket.accept();
				pool.submit(new Receiver(server));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
