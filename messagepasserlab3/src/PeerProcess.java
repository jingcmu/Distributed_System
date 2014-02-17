
import java.net.Socket;


public class PeerProcess {
	String processName;
	String 	ip;
	int port;
	Socket socket;
	
	public PeerProcess(String processName, String ip, int port) {
		super();
		this.processName = processName;
		this.ip = ip;
		this.port = port;

	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getProcessName() {
		return this.processName;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	
	
}
