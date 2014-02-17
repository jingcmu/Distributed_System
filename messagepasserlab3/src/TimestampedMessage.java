
public class TimestampedMessage extends Message implements Comparable <Object> {
	private static final long serialVersionUID = 1L;

	protected Timestamp messageTimestamp;
	
	public TimestampedMessage(String destination, String kind, Object data) {
		super(destination, kind, data);
	}

	public TimestampedMessage(Message msg) {
		super(msg);
	}

	public TimestampedMessage(TimestampedMessage msg) {
		super(msg);
		this.messageTimestamp = new Timestamp(msg.messageTimestamp);
	}
	
	public TimestampedMessage() {
		super();
	}

	public Timestamp getTimestamp() {
		return messageTimestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.messageTimestamp = timestamp;
	}
	
	public String print() {
		StringBuffer s = new StringBuffer();
		s.append("    Received Message : ");
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
		s.append("\n\n");
		return s.toString();
	}

	@Override
	public String toString() {
		return "TimestampedMessage [source="
				+ source + ", kind=" + kind + ", destination=" + destination
				+ ", sequenceNumber=" + sequenceNumber + ", duplicateFlag="
				+ duplicateFlag + ", data=" + data + ", timestamp=" + messageTimestamp.toString() + "]";
	}

	

	@Override
	public int compareTo(Object obj) {
		TimestampedMessage tsm = (TimestampedMessage) obj;
		int ret = 0;
		Timestamp ts = tsm.getTimestamp();
		ret = messageTimestamp.compareTo(ts);
		return ret;
		
	}

	public boolean isConcurrent(TimestampedMessage prev) {
		
		if (prev == null) {
			return false;
		}
		
		if (this.compareTo(prev) == 0) {
			return true;
		}
		return false;
	}
	
}
