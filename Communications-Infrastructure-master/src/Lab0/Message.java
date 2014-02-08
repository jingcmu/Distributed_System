package Lab0;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    static Logger logger = Logger.getLogger(Message.class);

    private int seqNum;
    private String src;
    private String dest;
    private String kind;
    private Object data;
    private boolean duplicate;

    /**
     * Constructor of message
     *
     * @param dest
     * @param kind
     * @param data
     */
    public Message(String dest, String kind, Object data) {
        this.dest = dest;
        this.kind = kind;
        this.data = data;
        this.duplicate = false;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
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

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public Message copyOf() {
        Message to = new Message(this.dest, this.kind, this.data);
        to.duplicate = this.duplicate;
        to.seqNum = this.seqNum;
        to.src = this.src;

        return to;
    }

    @Override
    public String toString() {
        return "From:" + this.getSrc() + " to:" + this.getDest() +
               " Seq:" + this.getSeqNum() + " Kind:" + this.getKind()
               + " Dup:" + this.getDuplicate() + " Data:" + this.getData();
    }
}
