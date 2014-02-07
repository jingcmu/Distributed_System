public class TimeStampedMessage extends Message {
	private TimeStamp timeStamp;
	
	public TimeStampedMessage(String dest, String kind, Object data){
		super(dest, kind, data);
	}
	
	public TimeStampedMessage(TimeStampedMessage timeStampedMessage) {
		super(timeStampedMessage);
		TimeStamp newTimeStamp = timeStampedMessage.getTimeStamp();
		if(newTimeStamp instanceof LogicalTimeStamp) {
			this.setTimeStamp(new LogicalTimeStamp((LogicalTimeStamp)newTimeStamp));
		} else {
			this.setTimeStamp(new VectorTimeStamp((VectorTimeStamp)newTimeStamp));
		}
	}

	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
}
