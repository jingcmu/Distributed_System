public class LogicalTimeStamp extends TimeStamp {
	private static final long serialVersionUID = 1L;
	private int localTime;
	
	public LogicalTimeStamp(String localName) {
		super(localName);
		this.localTime = 0;
	}
	
	public LogicalTimeStamp(LogicalTimeStamp logicalTimeStamp) {
		super(null);
		this.localName = logicalTimeStamp.getLocalName();
		this.localTime = logicalTimeStamp.getLocalTime();
	}
	
	public int compareTo(TimeStamp timeStamp) {
		LogicalTimeStamp logicalTimeStamp = (LogicalTimeStamp)timeStamp;
		if(this.localTime < logicalTimeStamp.getLocalTime()) {
			return -1;
		} else if(this.localTime > logicalTimeStamp.getLocalTime()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int getLocalTime() {
		return this.localTime;
	}
	
	public void setLocalTime(int localTime) {
		this.localTime = localTime;
	}
	
	public void updateTimeStamp(TimeStamp timeStamp) {
		this.localTime++;	// Increment local time
		
		/* Update localTime with new time */
		if(timeStamp != null && this.localTime < ((LogicalTimeStamp)timeStamp).getLocalTime() + 1) {
			this.localTime = ((LogicalTimeStamp)timeStamp).getLocalTime() + 1;
		}
	}
	
	public String printTimeStamp() {
		return String.valueOf(this.localTime);
	}
}
