import java.util.HashMap;
import java.util.List;

public class Configuration {
	private List<Node> configuration;
	private List<Rule> sendRules;
	private List<Rule> receiveRules;
	
	private List<Group> groups; //groups
	private HashMap<String, Group> hashgroup; //groups in hash form
	
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

	public List<Group> getGroups() {
		return groups;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public HashMap<String, Group> getHashroups() {
		return hashgroup;
	}	
	
    public void setHashroups() {
    	hashgroup = new HashMap<String, Group>();
    	for(Group g : groups) {
    		g.setNodes(configuration);
    		hashgroup.put(g.getName(), g);
    	}
	}

}
