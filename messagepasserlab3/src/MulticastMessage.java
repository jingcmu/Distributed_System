import java.io.Serializable;


public class MulticastMessage extends TimestampedMessage implements Comparable <Object>, Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String groupName;
	protected String origin;
	protected Timestamp multicastTimestamp;
	protected boolean NACK = false;
	
	
	public MulticastMessage() {
		super();
	}
	
	public MulticastMessage(Message msg, MulticastGroup g, String processName) throws Exception {
		super(msg);
		this.groupName = g.getGroupName();
		this.multicastTimestamp = g.getTimestamp();
		this.origin = processName;
	}
	
	
	public MulticastMessage(MulticastMessage m) {
		super((TimestampedMessage)m);
		
		this.groupName = m.groupName;
		this.origin = m.origin;
		this.multicastTimestamp = new Timestamp(m.multicastTimestamp);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	
	public String getOrigin() {
		return origin;
	}

	public Timestamp getMulticastTimestamp() {
		return multicastTimestamp;
	}

	public boolean isNACK() {
		return NACK;
	}

	public void setNACK(boolean nACK) {
		this.NACK = nACK;
	}

	public String print() {
		StringBuffer s = new StringBuffer();
		s.append("\tSource          = " + source 
				+ "\n\tKind            = " + kind
				+ "\n\tDestination     = " + destination 
				+ "\n\tSequence Number = " + sequenceNumber 
				+ "\n\tDuplicate Flag  = " + duplicateFlag
				+ "\n\tData            = " + data
				+ "\n\tTimestamp       = ");
		
		if (messageTimestamp != null) {
			s.append(messageTimestamp.toString());
		}
		else {
			s.append("null");
		}
		s.append("\n\tMulticast       = Group Name : " + groupName 
				+ "\tOrigin : " + origin 
				+ "\t Group Event # : ");
		if (multicastTimestamp != null) {
			s.append(multicastTimestamp.toString());
		}
		else {
			s.append("null");
		}
		s.append("\n\n");		
		return s.toString();
	}
	
	public void printHeaders() {
		StringBuffer s = new StringBuffer();
		s.append(
				"\nGroup Name : " + groupName 
				+ "\tOrigin : " + origin 
				+ "\t Group Event # : "
				);
		if (multicastTimestamp != null) {
			s.append(multicastTimestamp.toString());
		}
		else {
			s.append("null");
		}
		s.append("\tkind : " + kind);
		s.append("\tSource : " + source);
		
		Client.getMessageShower().setJTextStatus(s.toString());
		return;
	}
		
	@Override
	public int compareTo(Object obj) {
		MulticastMessage m = (MulticastMessage) obj;
		int ret = 0;
		Timestamp ts = m.getMulticastTimestamp();
		ret = multicastTimestamp.compareTo(ts);	
		return ret;
		
	}

	public void printMutexHeaders() {
		StringBuffer s = new StringBuffer();
		s.append("\tOrigin : ");
		s.append(origin);
		s.append("\tKind : ");
		s.append(kind + "\n");
		Client.getMessageShower().setJTextStatus(s.toString());
	}

}
