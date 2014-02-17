import java.lang.reflect.Array;


public class HoldBackMessage extends MulticastMessage {
	private static final long serialVersionUID = 1L;
	
	boolean [] receivedFromMap;
	MulticastGroup g = null;
	
	public boolean[] getReceivedFromMap() {
		return receivedFromMap;
	}

	public HoldBackMessage(MulticastMessage m, MulticastGroup g) {
		super(m);
		
		int size = g.getGroupSize();
		this.g = g;
		receivedFromMap = new boolean [size]; 
		
		for (int i = 0; i < size; i++) {
			receivedFromMap[i] = false;
		}
	}
	
	public HoldBackMessage(HoldBackMessage holdBackMessage) {
		// TODO Auto-generated constructor stub
		super((MulticastMessage)holdBackMessage);
	}

	public String print() {
		StringBuffer str = new StringBuffer("\tDeliveryMap : [");
		for (int i = 0; i < receivedFromMap.length; i++) {
			str.append(receivedFromMap[i] + ", ");
		}
		str.append("\b\b]");		
		Client.getMessageShower().setJTextStatus(str.toString());
		
		return super.print();
	}

	public void updateBitMap(int sourceIndex) {
		receivedFromMap[sourceIndex] = true;
		
	}

	public String printMap() {
		StringBuffer str = new StringBuffer("\tDeliveryMap : [");
		for (int i = 0; i < g.getGroupSize(); i++) {
			str.append(g.getNthMember(i) +  "=" + receivedFromMap[i] + ", ");
		}
		str.append("\b\b]");
		
		return str.toString();
	}

	public void printHoldBackEntry() {
		StringBuffer s = new StringBuffer();
		s.append("Group Name : " + groupName 
				+ "\tOrigin : " + origin 
				+ "\t Group Event # : ");
		if (multicastTimestamp != null) {
			s.append(multicastTimestamp.toString());
		}
		else {
			s.append("null");
		}
		
		s.append(this.printMap());
		
		Client.getMessageShower().setJTextStatus(s.toString());
		return;
	}

	public boolean deliveredtoAll() {
		for (int i = 0; i < receivedFromMap.length; i++) {
			if(receivedFromMap[i] == false)
				return false;
		}
		return true;
	}

	
}
