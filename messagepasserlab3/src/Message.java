
import java.io.Serializable;

public class Message implements Serializable, Cloneable {	
	
	private static final long serialVersionUID = 1L;
	protected String source;
	protected String kind;
	protected String destination;
	protected Long sequenceNumber;
	protected String duplicateFlag = "false";
	protected Object data; 


	public Message(String destination, String kind, Object data) {
		super();
		this.destination = destination;
		this.kind = kind;
		this.data = data;
	}

	public Message(Message msg) {
		super();
		this.source = msg.getSource();
		this.kind = msg.getKind();
		this.destination = msg.getDestination();
		this.sequenceNumber = msg.getSequenceNumber();
		this.duplicateFlag = msg.getDuplicateFlag();
		this.data = msg.getData();
		
	}

	public Message() {
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setDuplicateFlag(String duplicateFlag) {
		this.duplicateFlag = duplicateFlag;
	}

	public String getSource() {
		return source;
	}

	public String getKind() {
		return kind;
	}

	public String getDestination() {
		return destination;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public String getDuplicateFlag() {
		return duplicateFlag;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return "Message [source=" + source + ", kind=" + kind
				+ ", destination=" + destination + ", sequenceNumber="
				+ sequenceNumber + ", duplicateFlag=" + duplicateFlag
				+ ", data=" + data + "]";
	}	

	public Message clone() throws CloneNotSupportedException {
		return (Message) super.clone();
	}

	public String print() {
		StringBuffer s = new StringBuffer();
		s.append("\n\tSource          = " + source 
				+ "\n\tKind            = " + kind
				+ "\n\tDestination     = " + destination 
				+ "\n\tSequence Number = " + sequenceNumber 
				+ "\n\tDuplicate Flag  = " + duplicateFlag
				+ "\n\tData            = " + data);
		s.append("\n\n");
		return s.toString();
	}
	
}	