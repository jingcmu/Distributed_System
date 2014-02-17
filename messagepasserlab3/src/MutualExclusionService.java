import java.util.ArrayList;

public class MutualExclusionService {
	protected enum State {
		RELEASED, HELD, WANTED
	}

	protected enum MessageType {
		MUTEX_REQUEST, MUTEX_RELEASE, MUTEX_ACK
	}

	String processName;
	MessagePasser mp;
	MulticastService mcs;

	State state = State.RELEASED;
	boolean voted = false;

	MulticastGroup votingGroup;
	boolean[] bitMapACK;

	
	ArrayList<MulticastMessage> requestQueue = null;
	
	public MutualExclusionService(String processName, MessagePasser mp,
			MulticastService mcs) throws Exception {
		super();
		Client.getMessageShower().setJTextStatus(
				"Initializing Mutual Exclusion Service "
				+ State.HELD.toString() + "...\n");
		this.mp = mp;
		this.mcs = mcs;
		this.processName = processName;
		

		initVotingGroup();
		requestQueue = new ArrayList<MulticastMessage>();
		bitMapACK = new boolean[votingGroup.getGroupSize()];
	}

	/* DONE */
	public boolean requestMutex() throws Exception {
		String emptyString = "";
		state = State.WANTED;
		
		if (state == State.HELD) {
			return false;
		}

		for (int i = 0; i < bitMapACK.length; i++) {
			bitMapACK[i] = false;
		}

		TimestampedMessage m = new TimestampedMessage(emptyString,
				MessageType.MUTEX_REQUEST.toString(), emptyString);

		synchronized (this) {
			mcs.rMulticast(votingGroup.getGroupName(), m);
			Client.getMessageShower().setJLabelStatus("Waiting for voting . . .");
			Client.getMessageShower().setJTextStatus("Waiting for responses from voting group . . .\n");
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Client.getMessageShower().setJLabelStatus("Error!");
				Client.getMessageShower().setJTextStatus("Error waiting for replies!\n");
				throw e;
			}
			this.state = State.HELD;
		}
		
		return true;
	}

	/* DONE */
	protected synchronized void requestHandler(MulticastMessage m) {
	
		Client.getMessageShower().setJTextStatus("\tRequest to enter critical section received from " + m.getOrigin() + "\n");
		m.printMutexHeaders();
	
		Client.getMessageShower().setJTextStatus("\tBEFORE : state : " + state.toString()
				+ " voted : " + voted + "\n");
	
		if (state == State.HELD || this.voted == true) {
			Client.getMessageShower().setJTextStatus("\tEnqueued request from " + m.getOrigin() + " to enter critical section!\n");
			requestQueue.add(m);
		} else {
			Client.getMessageShower().setJTextStatus("\tGiving permission to " + m.getOrigin() + " to enter critical section!\n");
			sendReply(m);
			voted = true;
		}
	
		Client.getMessageShower().setJTextStatus("\tAFTER : state : " + state.toString()
				+ " voted : " + voted + "\n");
	}

	/* DONE */
	public boolean releaseMutex() {
		
		if (state != State.HELD) {
			return false;
		}
		String emptyString = "";
		state = State.RELEASED;
		TimestampedMessage m = new TimestampedMessage(emptyString,
				MessageType.MUTEX_RELEASE.toString(), emptyString);
		mcs.rMulticast(votingGroup.getGroupName(), m);
		
		return true;
	}

	protected synchronized void releaseHandler(MulticastMessage m) {
		Client.getMessageShower().setJTextStatus("\tMessage to release mutex received from " + m.getSource() + "\n");
		m.printMutexHeaders();
		
		if (requestQueue.size() < 1) {
			voted = false;
		}
		else {
			MulticastMessage msg = requestQueue.remove(0);
			Client.getMessageShower().setJTextStatus("\tGiving permission to " + msg.getOrigin() + " to enter critical section!\n");
			sendReply(msg);
			voted = true;
		}
		
	}

	public synchronized void replyHandler(Message msg) {
		try {
	
			synchronized (this) {
	
				Client.getMessageShower().setJTextStatus("Received ACK from  : " + msg.getSource() + "\n");
				String source = msg.getSource();
				int index = votingGroup.getIndex(source);
				int i;
	
				bitMapACK[index] = true;
	
				for (i = 0; i < bitMapACK.length; i++) {
					if (bitMapACK[i] == false) {
						break;
					}
				}
	
				if (i == bitMapACK.length) {
					Client.getMessageShower().setJTextStatus("\tReceived ACK from all members in votingGroup.\n");
					this.notifyAll();
				}
			}
	
		} catch (Exception e) {
			Client.getMessageShower().setJLabelStatus("Exception in replyhandler!");
			Client.getMessageShower().setJTextStatus("Exception in replyhandler");
			e.printStackTrace();
	
		}
	
	}

	private synchronized void initVotingGroup() throws Exception {
		ArrayList<MulticastGroup> groups = mcs.getGroups();
		String str = "";
		for (MulticastGroup group : groups) {
			if (group.getGroupName().equals("group_" + processName)) {
				this.votingGroup = group;
				str += ("Voting group for " + this.processName
						+ " set to : [" + this.votingGroup.getGroupName() + "]\n");
				str += group.print();
				Client.getMessageShower().setJTextGroupConfig(str);
				return;
			}
		}

		throw new Exception("Couldn't initialize voting group for "
				+ this.processName);
	}

	private synchronized void sendReply(MulticastMessage m) {
		String emptyString = "";
		Client.getMessageShower().setJTextStatus("\tSending unicast ACK to " + m.getOrigin()
				+ " of kind " + MessageType.MUTEX_ACK.toString() + "\n");
		TimestampedMessage tm = new TimestampedMessage(m.getOrigin(),
				MessageType.MUTEX_ACK.toString(), emptyString);

		mp.send(tm);

	}
}
