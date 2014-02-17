
public class UpdatingCount extends Thread{
	private Integer mutexMsgSentCount;
	private Integer mutexMsgRecvCount;
	public int getMutexMsgRecvCount() {
		return mutexMsgRecvCount;
	}

	public void setMutexMsgRecvCount(int mutexMsgRecvCount) {
		this.mutexMsgRecvCount = mutexMsgRecvCount;
	}
	
	public int getMutexMsgSentCount() {
		return mutexMsgSentCount;
	}

	public void setMutexMsgSentCount(int mutexMsgSentCount) {
		this.mutexMsgSentCount = mutexMsgSentCount;
	}

	public UpdatingCount() {
		setMutexMsgSentCount(0);
		setMutexMsgRecvCount(0);
	}
	
	public void run()
	   {
		   while(true) {
			   setMutexMsgSentCount(Client.getMp().getnMutexMsgSent());
			   setMutexMsgRecvCount(Client.getMp().getnMutexMsgReceived());
			   Client.getMessageShower().setJTextField4(mutexMsgSentCount.toString());
			   Client.getMessageShower().setJTextField5(mutexMsgRecvCount.toString());
		   }
	   }

	
}
