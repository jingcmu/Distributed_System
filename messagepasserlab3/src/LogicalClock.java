public class LogicalClock extends ClockService {
	
	LogicalClock() {
		super(1, 0);
	}

	@Override
	synchronized protected Timestamp getTimestamp() {
		
		int [] ticks = this.currentTime.getTicks();
		ticks[this.myIndex]++;
		
		return new Timestamp(this.currentTime);
	}

	@Override
	protected void updateTimestamp(Timestamp ts) {
		int [] cur_ticks = this.currentTime.getTicks();
		int [] ts_ticks = ts.getTicks();
		int max;
		
		if (cur_ticks[myIndex] > ts_ticks[myIndex]) {
			max = cur_ticks[myIndex];
		}
		else {
			max = ts_ticks[myIndex];
		}
		
		
		cur_ticks[myIndex] = max + 1;
		
		
		return;
		
		
	}

}
