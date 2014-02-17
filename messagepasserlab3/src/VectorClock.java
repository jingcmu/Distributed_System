
public class VectorClock extends ClockService {

	public VectorClock(int nProcesses, int processIndex) {
		super(nProcesses, processIndex);

	}

	protected Timestamp getTime() {
		return new Timestamp(this.currentTime);
	}
	
	@Override
	synchronized protected Timestamp getTimestamp() {
		int[] ticks = this.currentTime.getTicks();
		ticks[this.myIndex]++;
		return new Timestamp(this.currentTime);
	}

	@Override
	protected void updateTimestamp(Timestamp ts) {
		int[] cur_ticks = this.currentTime.getTicks();
		int[] ts_ticks = ts.getTicks();
		int max;

		
		for (int i = 0; i < cur_ticks.length; i++) {

			
			if (cur_ticks[i] > ts_ticks[i]) {
				max = cur_ticks[i];
			} else {
				max = ts_ticks[i];
			}
			
			if (i == myIndex) {
				cur_ticks[i] = max + 1;
			}
			else {
				cur_ticks[i] = max;
			}

		}

		return;
		
	}

}
