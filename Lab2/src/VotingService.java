import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;


public class VotingService {
	
	public VotingStatus status;
	
	public HashSet<String> receivedAcks;
	
	public MessagePasser messagePasser;
	
	public Group votingGroup;
	
	public boolean voted;
	
	public PriorityBlockingQueue<TimeStampedMessage> requestsQueue;
	
	public VotingService(MessagePasser messagePasser) {
		this.status = VotingStatus.RELEASED;
		this.receivedAcks = new HashSet<String>();
		this.messagePasser = messagePasser;
		this.voted = false;
		this.requestsQueue = new PriorityBlockingQueue<TimeStampedMessage>(1024);
	}
	
	public void request(String data) {
		TimeStampedMessage message = new TimeStampedMessage(null, votingGroup.getName(), MessageKind.REQUEST.toString(), data);
		status = VotingStatus.WANTED;
		receivedAcks = new HashSet<String>();
		messagePasser.send(message);
	
	}
	
	public void receiveRequest(TimeStampedMessage message) {
		if (voted == true || status == VotingStatus.HOLD) {
			requestsQueue.offer(message);
		} else {
			voted = true;
			TimeStampedMessage ackMessage = new TimeStampedMessage(message.oriSrc, null, MessageKind.ACK.toString(), message.data);
			messagePasser.send(ackMessage);
		}
	}
	
	public void receiveAck(Message message) {
		receivedAcks.add(message.oriSrc);
		if (receivedAcks.size() >= votingGroup.getNodes().size()) {
			status = VotingStatus.HOLD;
		}
	}
	
	public void release() {
		status = VotingStatus.RELEASED;
		TimeStampedMessage message = new TimeStampedMessage(null, votingGroup.getName(), MessageKind.RELEASE.toString(), "");
		messagePasser.send(message);
	}
	
	public void receiveRelease(Message message) {
		if (requestsQueue.size() > 0) {
			Message request = requestsQueue.poll();
			voted = true;
			TimeStampedMessage ackMessage = new TimeStampedMessage(request.oriSrc, null, MessageKind.ACK.toString(), request.data);
			messagePasser.send(ackMessage);
		} else {
			voted = false;
		}
	}

}
