public class VectorClockService extends ClockService {
	private VectorTimeStamp timeStamp;
	
	public VectorClockService(String configFileName, String localName) {
		super(configFileName, localName);
		this.timeStamp = new VectorTimeStamp(this.localName, this.config.getConfiguration());
	}
	
	public TimeStamp getTimeStamp() {
		this.timeStamp.updateTimeStamp(null);
		return new VectorTimeStamp(this.timeStamp);
	}
	
	public void updateTimeStamp(TimeStamp timeStamp) {
		this.timeStamp.updateTimeStamp(timeStamp);
	}
}
