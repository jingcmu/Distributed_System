import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String src;
	protected String dest;
	protected String kind;
	protected Object data;
	protected Integer seqNum;
	protected Boolean dupe;
	protected Boolean isMulticast;
	
	public Message(String dest, String kind, Object data){
		this.dest = dest;
		this.kind = kind;	
		this.data = data;
		this.dupe = false;
	}
	
	public Message(Message msg){
		this.src = msg.getSrc();
		this.dest = msg.getDest();
		this.kind = msg.getKind();
		this.data = msg.getData();
		this.seqNum = msg.getSeqNum();
		this.dupe = msg.isDupe();
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDest() {
		return dest;
	}

	public String getKind() {
		return kind;
	}

	public Object getData() {
		return data;
	}

	public Integer getSeqNum() {
		return seqNum;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public Boolean isDupe() {
		return dupe;
	}

	public void setDupe(Boolean dupe) {
		this.dupe = dupe;
	}

	public Boolean getIsMulticast() {
		return isMulticast;
	}

	public void setIsMulticast(Boolean isMulticast) {
		this.isMulticast = isMulticast;
	}
	
	
}
