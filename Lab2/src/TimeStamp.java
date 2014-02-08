import java.io.Serializable;

public abstract class TimeStamp implements Serializable, Comparable<TimeStamp> {
	private static final long serialVersionUID = 1L;
	protected String localName;
	
	public TimeStamp(String localName) {
		this.localName = localName;
	}

	public String getLocalName() {
		return localName;
	}
	
	/*
	 * Update local TimeStamp according to timeStamp received
	 */
	public abstract void updateTimeStamp(TimeStamp timeStamp);
	
	/*
	 * Return TimeStamp in printable format
	 */
	public abstract String printTimeStamp();
}
