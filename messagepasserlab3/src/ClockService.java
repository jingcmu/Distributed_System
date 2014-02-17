
public abstract class ClockService {

	//private ClockType clock;
	protected Timestamp currentTime;
	protected int myIndex = 0;
	
	public ClockService(int size, int myIndex) {
		super();
		int [] ticks = new int [size];
		this.currentTime = new Timestamp(ticks);
		this.myIndex = myIndex;
	}

	protected abstract Timestamp getTimestamp();
	
	protected abstract void updateTimestamp(Timestamp ts);
	
}
