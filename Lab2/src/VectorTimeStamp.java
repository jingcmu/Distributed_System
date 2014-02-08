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

	public void updateTimeStamp(TimeStamp timeStamp) {
		/* Increment timeVector */
		int localTime = this.timeVector.get(this.localName);
		localTime++;
		this.timeVector.put(this.localName, localTime);
		
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
	
	public void updateTimeStamp(TimeStamp timeStamp, String src) {
		/* Increment timeVector */
		int srcTime = this.timeVector.get(src);
		srcTime++;
		this.timeVector.put(src, srcTime);
		
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
