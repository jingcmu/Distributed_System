package Lab0;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;

@SuppressWarnings("serial")
public class Message implements Serializable {

	public String src;

	public String dest;

	public String kind;

	public int seq;

	public boolean dupe;

	public Object data;
	
	public String log;

	public Message(String dest, String kind, Object data) {
		this.dest = dest;
		this.kind = kind;
		this.data = data;
	}

	public Message(Message message) {
		this.dest = message.dest;
		this.src = message.src;
		this.kind = message.kind;
		this.data = message.data;
		this.seq = message.seq;
	}

	// These settors are used by MessagePasser.send, not your app
	public void set_source(String source) {
		this.src = source;
	}

	public void set_seqNum(int sequenceNumber) {
		this.seq = sequenceNumber;
	}

	public void set_duplicate(Boolean dupe) {
		this.dupe = dupe;
	}

	// other accessors, toString, etc as needed
	public byte[] toBytes() throws IOException, UnknownHostException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(this);
		oout.flush();
		byte[] result = bout.toByteArray();
		return result;
	}

	public static Message getMessage(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bint = new ByteArrayInputStream(bytes);
		ObjectInputStream oint = new ObjectInputStream(bint);
		Message result = (Message) oint.readObject();
		return result;
	}
	
	@Override
	public String toString() {
		return "src: " + src + ", dest: " + dest + ", kind: " + kind + ", data: " + data + ", seq: " + seq;
	}

}
