import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class MulticastGroup {
	String groupName;
	VectorClock clock = null;
	
	ArrayList<String> members = null;
	ArrayList<HoldBackMessage> holdbackQueue = null;
	private MessagePasser mp;
	private MulticastService mcs;
	private Timestamp lastDelivered = null;
	
	public ArrayList<HoldBackMessage> getHoldbackQueue() {
		return holdbackQueue;
	}
	
	public MulticastGroup() {
		super();
		this.members = new ArrayList<String>();
		this.holdbackQueue = new ArrayList<HoldBackMessage>();
	}

	public MulticastGroup(String groupName, MessagePasser mp, MulticastService mcs) {
		super();
		this.groupName = groupName;
		this.members = new ArrayList<String>();
		this.holdbackQueue = new ArrayList<HoldBackMessage>();
		this.mp = mp;
		this.mcs = mcs;
		
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the members
	 */
	public ArrayList<String> getMembers() {
		return members;
	}
	
	public void addMember(String member) {
		this.members.add(member);
	}
	
	public String print() {
		String str = ("\nGroup name : " + this.groupName + "\nMembers : \n");
		for (String member : members) {
			str += ("\t" + member + "\n");
		}
		str += ("\n");
		return str;
	}
	
	public int getGroupSize() {
		return members.size();
	}
	
	public int getIndex(String processName) {
		
		return getIndexInStringArrayList(members, processName);
		
	}

	/**
	 * @param clock the clock to set
	 */
	public void setClock(VectorClock clock) {
		this.clock = clock;
		this.lastDelivered = new Timestamp(clock.getTime());
	}

	public synchronized Timestamp getTimestamp() throws Exception {
		if (this.clock == null) {
			throw new Exception("Clock not initialized for multicast group : " + groupName);
		}
		
		return clock.getTimestamp();
	}
	
	public synchronized ArrayList<String> processIncomingMessage(MulticastMessage m) {
		
		HoldBackMessage h;
		boolean foundInQueue = false;
		
		Client.getMessageShower().setJTextStatus("\n  Received Multicast Message : \n");
		m.printHeaders();
		
		// Received messages from past! 
		if (m.getMulticastTimestamp().compareTo(clock.getTime()) < 0) {
			
			int originIndex = getIndex(m.getOrigin());
			
			if (originIndex == getIndex(mcs.processName) 
					&& m.multicastTimestamp.ticks[originIndex] > lastDelivered.ticks[originIndex]) {
				Client.getMessageShower().setJTextStatus("Delayed message from self received");
				
			}
			
			else if (m.isNACK() == true) {
			
				Client.getMessageShower().setJTextStatus("Message from past received with [NACK] flag = true.\n"
						+ m.getSource() + " might not have received message from me."
						+ "\nSend him the message back so that he knows i got the message in the past.");
				
				ArrayList <String> pendingRecipients = new ArrayList <String>();
				m.setNACK(false);
				pendingRecipients.add(m.getSource());
				return pendingRecipients;
			}
			else {
				Client.getMessageShower().setJTextStatus("Old message with [NACK] = false. Ignore!");
				return null;
			}
			
		}
		
		
		h = findInHoldBackQueue(m);
		
		if (h != null) {
			foundInQueue = true;
		}
		else {
			h = new HoldBackMessage(m, this);
			synchronized (holdbackQueue) {
				holdbackQueue.add(h);
				Collections.sort(holdbackQueue);
			}

		}
		
		int sourceIndex = getIndex(m.getSource());
		h.updateBitMap(sourceIndex);
		
		// Message delivered to all.
		// 1. Remove from queue 
		// 2. updated time stamp 
		// 3. delivered to client
		if (h.deliveredtoAll() == true) {
			
			flushHoldBackQueue();

			return null;
		}
		
		// This is the first time this process got this message,
		// need to send ACK to every process in group.
		// only of not a message from self.
		
		if (foundInQueue == false ) {
			if (getIndex(m.getOrigin()) != getIndex(mcs.processName)) {
				return members;
			}
			else {
				// i am the origin of these messages! 
				return null;
			}
		}
		else {
			// we must have already send a broadcast to ack receipt of this message.
			
		}
		
		// DO nothing
		return null;
	}

	private synchronized void flushHoldBackQueue() {
		synchronized (holdbackQueue) {
			
		
		if (holdbackQueue.size() < 1) {
			Client.getMessageShower().setJLabelStatus("No messages delivered!");
			return;
		}
		
		printHoldBackQueue();
		Client.getMessageShower().setJTextStatus(
				"In Flush queue : current Time = " + clock.getTime().toString() + "\n");
		
		//HoldBackMessage first = new HoldBackMessage(holdbackQueue.get(0));
		
		// check all messages which are concurrent to first message in queue.
		// if delivered to all recipients, then add to mp.receiveQueue
		for (Iterator<HoldBackMessage> itr = holdbackQueue.iterator(); itr.hasNext(); ) {
			HoldBackMessage msg = itr.next();
			
			if (msg.deliveredtoAll() == false) {
				continue;
			}
			Client.getMessageShower().setJTextStatus(
					"\tMessage timestamp = " + msg.getMulticastTimestamp().toString() + "\n");
			
			int [] currTicks = clock.currentTime.getTicks(); 
			int [] msgTicks = msg.getMulticastTimestamp().getTicks();
			int sourceIndex  = this.getIndex(msg.getOrigin());
			
			// message from self.. immediately co-deliver 
			if (sourceIndex == getIndex(mcs.processName) && msgTicks[sourceIndex] == lastDelivered.ticks[sourceIndex] + 1) {
				Client.getMessageShower().setJTextStatus("Delivering message from self!\n");
				itr.remove();
				COdeliver(msg);
			}
			
			int i;
			for (i = 0; i < currTicks.length; i++) {
				if (i == sourceIndex) {
					continue;
				}
				
				if (msgTicks[i] > currTicks[i]) {
					break;
				}
			}
			
			// from others 
			if (i == currTicks.length && currTicks[sourceIndex] + 1 == msgTicks[sourceIndex]) {
				Client.getMessageShower().setJLabelStatus("Delivering message!");
				itr.remove();
				COdeliver(msg);
				
			}
			
			
			
		}
		
		Client.getMessageShower().setJTextStatus(
			"After flush, current timestamp = " + clock.getTime().toString() + "\n");
		printHoldBackQueue();
		}
	}

	

	private synchronized void COdeliver(HoldBackMessage current) {
		int index = this.getIndex(current.getOrigin());
		
		if (current.getOrigin().equals(mcs.processName)) {
			// message from self. do not merge time stamps.
			// no need to count event twice.
			lastDelivered.ticks[index] += 1;
			Client.getMessageShower().setJTextStatus("\nCo delivering message from self. lastDelivered = " 
										+ lastDelivered.toString() + "\n");
		}
		else {
			Client.getMessageShower().setJTextStatus("\nBefore CO delivering message Timestamp : " 
										+ clock.currentTime.toString() + "\n");
			clock.currentTime.ticks[index] += 1;
			Client.getMessageShower().setJTextStatus("\nAfter Merge Timestamp : " 
										+ clock.currentTime.toString() + "\n");
			
		}
		
		mp.deliverToClient(current);
		
	}

	private synchronized HoldBackMessage findInHoldBackQueue(MulticastMessage m) {
		synchronized (holdbackQueue) {
			
		for (HoldBackMessage h : holdbackQueue) {
			if (h.getOrigin().equals(m.getOrigin()) == true 
					&& h.getMulticastTimestamp().compareTo(m.getMulticastTimestamp()) == 0) {
				return h;
			}
		}
		}
		return null;
	}

	public String getNthMember(int i) {
		
		return members.get(i);
	}
	
	public void printHoldBackQueue() {
		synchronized (holdbackQueue) {
			
		for (HoldBackMessage h : holdbackQueue) {
			h.printHoldBackEntry();
		}
		}
	}
	
	public int getIndexInStringArrayList(ArrayList<String> elements, String member) {
		int index = 0;
		for (String element : elements) {
			if (member.equals(element)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public boolean isMember(String processName) {
		for (String member : members) {
			if (member.equals(processName)) {
				return true;
			}
		}
		return false;
	}
}
