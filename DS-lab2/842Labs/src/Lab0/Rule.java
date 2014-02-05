package Lab0;

public class Rule {
	
	public Action action;
	
	public String src;
	
	public String dest;
	
	public String kind;
	
	public Boolean dupe;
	
	public int seq;
	
	public boolean match(Message message) {
		if (src != null && !message.src.equals(src)) {
			return false;
		}
		if (dest != null && !message.dest.equals(dest)) {
			return false;
		}
		if (kind != null && !message.kind.equals(kind)) {
			return false;
		}
		if (seq > 0 && message.seq != seq) {
			return false;
		}
		if (dupe != null && message.dupe != dupe) {
			return false;
		}
		
		return true;
	}

}
