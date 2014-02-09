public class LogicalClockService extends ClockService {
	private LogicalTimeStamp timeStamp;
	
	public LogicalClockService(String configFileName, String localName) {
		super(configFileName, localName);
		this.timeStamp = new LogicalTimeStamp(this.localName);
	}
	
	public TimeStamp getTimeStamp() {
		this.timeStamp.updateTimeStamp(null);
		return new LogicalTimeStamp(this.timeStamp);
	} 
	
	public void updateTimeStamp(TimeStamp timeStamp) {
		this.timeStamp.updateTimeStamp(timeStamp);
	}
}
