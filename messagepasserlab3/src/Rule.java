

public class Rule {
	String type;
	String action;
	String source;
	String destination;
	Long sequenceNumber;
	String duplicate;
	
	
	public Rule(String type, String action, String source, String destination,
			Long sequenceNumber, String duplicate) {
		super();
		this.type = type;
		this.action = action;
		this.source = source;
		this.destination = destination;
		this.sequenceNumber = sequenceNumber;
		this.duplicate = duplicate;
	}


	public String getType() {
		return type;
	}


	public String getAction() {
		return action;
	}


	public String getSource() {
		return source;
	}


	public String getDestination() {
		return destination;
	}


	public Long getSequenceNumber() {
		return sequenceNumber;
	}


	public String getDuplicate() {
		return duplicate;
	}	
	
}
