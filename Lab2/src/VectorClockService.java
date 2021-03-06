public class VectorClockService extends ClockService {
	private VectorTimeStamp timeStamp;
	
	public VectorClockService(String configFileName, String localName) {
		super(configFileName, localName);
		this.timeStamp = new VectorTimeStamp(this.localName, this.config.getConfiguration());
	}
	
	public TimeStamp getTimeStamp() {
		return this.timeStamp;
	}
	
}
