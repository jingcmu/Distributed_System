import java.util.List;

public class Configuration {
	private List<Node> configuration;
	private List<Rule> sendRules;
	private List<Rule> receiveRules;
	
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
}
