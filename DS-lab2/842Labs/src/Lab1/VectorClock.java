package Lab1;

import java.util.Arrays;

public class VectorClock implements ClockService<int[], TimeStampedMessage> {

	/* IDs can be assigned in MessagePasser after parsing config file: alice 1, bob 2, charlie 3 */
	/* have added id assignment in ConfigHandler */
	private int id; 
					
	private int[] timestamp;

	public VectorClock(int size, int id) {
		this.id = id;
		/* Here, size = total number of users (Every user has his own timestamp) */
		this.timestamp = new int[size];
		for (int i = 0; i < timestamp.length; i++) {
			timestamp[i] = 1;
		}
	}

	@Override
	public int[] increment() {
		timestamp[id]++;
		return timestamp;
	}

	@Override
	public int[] getTimeStamp() {
		return timestamp;
	}

	@Override
	public int compare(int[] message1, int[] message2) {

		int i;
		/* message 1 is less than or equal to message 2 */
		for (i = 0; i < message1.length; i++) {
			if (message1[i] <= message2[i])
				continue;
			break;
		}
		if (i == message1.length)
			return -1;

		/* message 2 is less than or equal to message 1 */
		for (i = 0; i < message2.length; i++) {
			if (message2[i] <= message1[i])
				continue;
			break;
		}
		if (i == message1.length)
			return 1;

		/* message1 == message2 */
		return 0;
	}

	@Override
	public void clockSynchronize(TimeStampedMessage msg) {

		for (int i = 0; i < timestamp.length; i++) {
			timestamp[i] = Math.max(timestamp[i],
					((int[]) msg.getTimeStamp())[i]);
		}
		timestamp[id]++;

	}

	@Override
	public boolean equal(int[] message1, int[] message2) {
		int i;
		for (i = 0; i < message1.length; i++) {
			if (message1[i] == message2[i])
				continue;
			break;
		}
		if (i != message1.length)
			return false;
		else
			return true;
	}
	
	public void print() {
		System.out.println("current timestamp: " + Arrays.toString(timestamp));
	}
	
	public String getTimeStampString(Object timestamp) {
		return Arrays.toString((int[])timestamp);
	}

}

