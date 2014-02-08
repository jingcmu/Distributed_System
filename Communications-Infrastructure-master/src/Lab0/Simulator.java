package Lab0;

import java.io.IOException;
import java.net.UnknownHostException;

public class Simulator {
	static public UserInputThread uiThread;
	private static String[] arg;
	
	public static void startUIThread() {
		uiThread = new UserInputThread();
        Thread thread = new Thread(uiThread); 
        thread.start();
    }
	/**
	 * For test.
	 */
	public static void main(String[] args) throws IOException {
		try {
			arg = UserInputThread.init();
            MessagePasser messagePasser = MessagePasser.getInstance(arg[0], arg[1]);	
			messagePasser.startListener();
			messagePasser.startSender();		
			startUIThread();
	        //messagePasser.teminate();			
				
		} catch (UnknownHostException e) { 
			// Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
