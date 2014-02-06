import java.util.HashMap;
import java.util.List;

public class Configuration {
	private List<Node> configuration;
	private List<Rule> sendRules;
	private List<Rule> receiveRules;
	
	private HashMap<String, Group> groups; //groups
	
	public List<Node> getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(List<Node> configuration) {
		this.configuration = configuration;
	}
	
	public List<Rule> getSendRules() {
		return sendRules;
	}
	
	public void setSendRules(List<Rule> sendRules) {
		this.sendRules = sendRules;
	}
	
	public List<Rule> getReceiveRules() {
		return receiveRules;
	}
	
	public void setReceiveRules(List<Rule> receiveRules) {
		this.receiveRules = receiveRules;
	}

	public HashMap<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(HashMap<String, Group> groups) {
		this.groups = groups;
	}

	
	
	
}
