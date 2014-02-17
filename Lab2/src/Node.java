import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class Node{
	private String name;
	private String ip;
	private Integer port;
	private ConcurrentHashMap<Integer, Boolean> received = new ConcurrentHashMap<Integer, Boolean>();
	private Group votingGroup;
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}

	public ConcurrentHashMap<Integer, Boolean> getReceived() {
		return received;
	}

	public void setReceived(ConcurrentHashMap<Integer, Boolean> received) {
		this.received = received;
	}

	public Group getVotingGroup() {
		return votingGroup;
	}

	public void setVotingGroup(Group votingGroup) {
		this.votingGroup = votingGroup;
	}

	
	
	
}
