import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorTimeStamp extends TimeStamp {
	private static final long serialVersionUID = 1L;
	private Map<String, Integer> timeVector;

	public VectorTimeStamp(String localName, List<Node> configuration) {
		super(localName);
		this.timeVector = new HashMap<String, Integer>();
		for (Node n : configuration) {
			this.timeVector.put(n.getName(), 0);
		}
	}
	
	public int compareTo(TimeStamp timeStamp) {
		int result = 2;
		VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)timeStamp;
		for(String s : this.timeVector.keySet()) {
			int localTime = this.timeVector.get(s);
			int newTime = vectorTimeStamp.getLocalTime().get(s);
			if(result == 2) {
				if(localTime < newTime) {
					result = -1;
				} else if(localTime > newTime) {
					result = 1;
				} else {
					result = 0;
				}
			} else if (result == -1) {
				if(localTime > newTime) {
					result = 0;
					return result;
				}
			} else if (result == 1) {
				if(localTime < newTime) {
					result = 0;
					return result;
				}
			} else if (result == 0) {
				if(localTime < newTime) {
					result = -1;
				} else if(localTime > newTime) {
					result = 1;
				}
			}
		}
		return result;
	}

	public VectorTimeStamp(VectorTimeStamp vectorTimeStamp) {
		super(null);
		this.localName = vectorTimeStamp.getLocalName();
		this.timeVector = new HashMap<String, Integer>(
				vectorTimeStamp.getLocalTime());
	}

	public Map<String, Integer> getLocalTime() {
		return this.timeVector;
	}
	
	@Override
	public void increment() {
		increment(localName);
	}
	
	public void increment(String key) {
		/* Increment timeVector */
		timeVector.put(key, timeVector.get(key) + 1);
	}

	public void updateTimeStamp(TimeStamp timeStamp) {
		increment(localName);
		
		/* Update timeVector with new time vector */
		if (timeStamp != null) {
			for (String s : this.timeVector.keySet()) {
				int time = this.timeVector.get(s);
				int updatedTime = ((VectorTimeStamp)timeStamp).getLocalTime().get(s);
				if (updatedTime > time) {
					this.timeVector.put(s, updatedTime);
				}
			}
		}
	}
	
	public void updateTimeStampMulticast(TimeStampedMessage message) {
		increment(message.oriSrc);
	}
	
	/**
	 * Ensure casual order
	 * @param message
	 * @param group 
	 * @return
	 */
	public boolean isOrdered(TimeStampedMessage message) {
		VectorTimeStamp messageTimeStamp = ((VectorTimeStamp)message.getTimeStamp());
		int mySrc = timeVector.get(message.getOriSrc());
		int src = messageTimeStamp.getLocalTime().get(message.getOriSrc());
		//System.out.println(printTimeStamp());
		//System.out.println(message.getTimeStamp().printTimeStamp());
		if (mySrc + 1 < src) {
			System.out.println(message.getOriSrc());
			return false;
		}
		
		for (String key : timeVector.keySet()) {
			if (!key.equals(message.getOriSrc()) && messageTimeStamp.getLocalTime().get(key) > timeVector.get(key)) {
				//System.out.println(key);
				return false;
			}
		}
		
		return true;
	}
	
	public String printTimeStamp() {
		StringBuilder result = new StringBuilder();
		result.append("[ ");
		for(String s : this.timeVector.keySet()) {
			int time = this.timeVector.get(s);
			result.append("(" + s + ": " + time + ") ");
		}
		result.append("]");
		return new String(result);
	}

	
}
