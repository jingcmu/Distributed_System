import java.util.ArrayList;

public class NACKSender extends Thread {

	MulticastService ms = null;
	ArrayList<MulticastGroup> groups = null;
	private long retryInterval = 10000; // default

	public NACKSender() {

	}

	public NACKSender(MulticastService ms, long retryInterval) {
		super();
		this.ms = ms;
		this.groups = ms.getGroups();
		this.retryInterval = retryInterval;
	}

	
	public void run() {
		while (true) {
			try {
				for (MulticastGroup group : groups) {
					sendNackstoGroup(group);
				}

				sleep(this.retryInterval);
			} catch (Exception e) {
				Client.getMessageShower().setJLabelStatus("Exception - NACKSender!");
				Client.getMessageShower().setJTextStatus("NACKSender caught Exception "
						+ e.toString());
				e.printStackTrace();
			}
		}

	}
	
	public void sendNackstoGroup(MulticastGroup group) {
		ArrayList<HoldBackMessage> holdbackQueue = group.getHoldbackQueue();
		synchronized (holdbackQueue) {
			
		for (HoldBackMessage h : holdbackQueue) {
			sendNackforMessage(group, h);
		}
		}
	}

	private void sendNackforMessage(MulticastGroup group, HoldBackMessage h) {
		
		// send to peers from whom we have not got this message, 
		// as they might not have received the message yet.
		
		ArrayList<String> pendingRecipients = new ArrayList<String>();
		ArrayList<String> members = group.getMembers();
		
		boolean [] bitMap;
		bitMap = h.getReceivedFromMap();
		
		for (int i = 0; i < bitMap.length; i++) {
			if (bitMap[i] == false) {
				String pendingRecepient = members.get(i);
				pendingRecipients.add(pendingRecepient);
			}
		}
		Client.getMessageShower().setJTextStatus("\n\tNACK sender : \n");
		h.printHeaders();
		Client.getMessageShower().setJTextStatus("\t" + h.printMap());
		Client.getMessageShower().setJTextStatus("\t\tSending NACK messages to  : " + pendingRecipients.toString());
		
		MulticastMessage m = new MulticastMessage((MulticastMessage)h);
		m.setNACK(true);
		ms.multicast(pendingRecipients, m);
		
		return;
	}

}