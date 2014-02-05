package Lab1;
import Lab0.Message;


public class TimeStampedMessage extends Message implements Comparable{
	
	private ClockService clockservice;
	
	/* This timestamp is used by both Logical(timestamp is simple int) & Vector(timestamp is int array) classes
	 * So, I'm using the type - Object
	 *  */
	private Object timeStamp;
	
	public TimeStampedMessage(String dest, String kind, String data, String log, ClockService clockservice) {
		super(dest, kind, data);
		this.clockservice = clockservice;
		this.log = log;
	}
	
	public TimeStampedMessage(TimeStampedMessage message) {
		super(message);
		this.timeStamp = message.timeStamp;
		this.clockservice = message.clockservice;
	}
	
	public ClockService getClockService(){
		return clockservice;
	}

	public Object getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Object timeStamp) {
		this.timeStamp = timeStamp;
		if (clockservice instanceof VectorClock) {
			int[] oldStamp = (int[])timeStamp;
			int[] newStamp = new int[oldStamp.length];
			for (int i = 0; i < oldStamp.length; i++) {
				newStamp[i] = oldStamp[i];
			}
			this.timeStamp = newStamp;
		}
	}
	
	public int compare(TimeStampedMessage message)
    {
        return clockservice.compare(timeStamp, message.getTimeStamp());
    }
	
	public boolean equal(TimeStampedMessage message)
    {
        return clockservice.equal(timeStamp, message.getTimeStamp());
    }

	public int compareTo(TimeStampedMessage message) {
		return clockservice.compare(timeStamp, message.getTimeStamp());
	}

	@Override
	public int compareTo(Object o) {
		return clockservice.compare(timeStamp, ((TimeStampedMessage)o).getTimeStamp());
	}
	
	@Override
	public String toString() {
		String ans = super.toString();
		ans = ans + ", timestamp: " + clockservice.getTimeStampString(timeStamp);
		return ans;
	}
}
