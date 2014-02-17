import java.io.Serializable;
import java.util.Arrays;

public class Timestamp implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	protected int[] ticks;

	public Timestamp() {
		super();
	}

	public Timestamp(int[] ticks) {
		super();
		this.ticks = ticks;
	}

	public Timestamp(Timestamp t) {
		super();

		int[] ticks = t.getTicks();
		int[] new_ticks = new int[ticks.length];

		for (int i = 0; i < ticks.length; i++) {
			new_ticks[i] = ticks[i];
		}

		this.ticks = new_ticks;

	}

	public int[] getTicks() {
		return ticks;
	}

	public void setTicks(int[] ticks) {
		this.ticks = ticks;
	}

	@Override
	public String toString() {
		return Arrays.toString(ticks);
	}

	public int compareTo(Timestamp ts) {
		
		int[] myTicks = this.getTicks();
		int[] tsTicks = ts.getTicks();
		
		if (myTicks.length != tsTicks.length) {
			// Different types of Time stamps passed for comparison. Can't be compare.
			return 0;
		}
		
		// Check if Logical Time stamp
		if (myTicks.length == 1) {
			return myTicks[0] - tsTicks[0];
		}
		
		// For vector time stamps, either all should be >= or all should be <= 
		int size = myTicks.length;
		boolean foundGreater = false;
		boolean foundLesser = false;
		
		for (int i = 0; i < size; i++) {
			if (myTicks[i] == tsTicks[i]) {
				continue;
			}
			else if (myTicks[i] < tsTicks[i]) {
				if (foundGreater == true)
					return 0;
				foundLesser = true;
			}
			else {
				if (foundLesser == true) {
					return 0;
				}
				foundGreater = true;
			}
		}
		
		if (foundGreater == true) {
			return 1;
		}
		else if (foundLesser == true) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
