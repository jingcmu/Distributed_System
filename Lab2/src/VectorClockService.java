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
	
	/**
	 * V(g, i)[ j]  := V(g, i)[ j]  + 1
	 */
	public void updateTimeStamp(TimeStampedMessage message) {
		this.timeStamp.updateTimeStamp(message.getTimeStamp(), message.src);
	}

	@Override
	public void increment() {
		timeStamp.getLocalTime().put(localName, timeStamp.getLocalTime().get(localName) + 1);
		
	}
	
	/**
	 * Ensure casual order
	 * @param message
	 * @param group 
	 * @return
	 */
	public boolean isOrdered(TimeStampedMessage message, Group group) {
		VectorTimeStamp messageTimeStamp = ((VectorTimeStamp)message.getTimeStamp());
		int mySrc = timeStamp.getLocalTime().get(message.getSrc());
		int src = messageTimeStamp.getLocalTime().get(message.getSrc());
		if (mySrc + 1 < src) {
			return false;
		}
		
		for (Node node : group.getNodes()) {
			String key = node.getName();
			if (messageTimeStamp.getLocalTime().get(key) > timeStamp.getLocalTime().get(key)) {
				return false;
			}
		}
		
		return true;
	}
}
