import java.io.Serializable;

public abstract class TimeStamp implements Serializable, Comparable<TimeStamp> {
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
