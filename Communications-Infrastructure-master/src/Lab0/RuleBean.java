package Lab0;

import org.apache.log4j.Logger;

public class RuleBean {
    static Logger logger = Logger.getLogger(RuleBean.class);

    public enum RuleAction {
        DROP, DUPLICATE, DELAY, NONE;
    }

    private RuleAction action;
    private String src;
    private String dest;
    private String kind;
    private Boolean duplicate = null;
    private int seqNum = -1;

    public RuleBean() {

    }

    public RuleBean(RuleAction action) {
        super();
        this.action = action;
    }

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
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

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public boolean isMatch(Message message) {
        if(getSrc() == null || getSrc().equalsIgnoreCase(message.getSrc())) {
            if(getDest() == null || getDest().equalsIgnoreCase(message.getDest())) {
                if(getKind() == null || getKind().equals(message.getKind())) {
                    if(getSeqNum() == -1 || getSeqNum() == message.getSeqNum()) {
                        if(getDuplicate() == null || getDuplicate() == message.getDuplicate()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "[action=" + action + ", src=" + src + ", dest=" + dest +
               ", kind=" + kind + ", SeqNum=" + seqNum + "]";
    }
}
