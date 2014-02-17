
public class PeerConnector extends Thread {

	private MessagePasser mp;
	private long retryInterval = 2000; // default

	public PeerConnector(MessagePasser messagePasser, long retryInterval) {
		this.mp = messagePasser;
		if (retryInterval > 0) {
			this.retryInterval = retryInterval;
		}
	}

	public void run() {

		while (true) {
			try {

				for (PeerProcess peer : mp.getPeers()) {
					/* open connection only if not already established. */
					if (peer.getSocket() == null) {
						mp.establishConnection(peer);
					}
				}
				sleep(this.retryInterval);

			} catch (Exception e) {
				e.printStackTrace();
				Client.getMessageShower().setJLabelStatus("PeerConnector got exception!");
			}
		}
	}

}
