


import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;



public class MessageListener extends Thread {
	   private Socket socket;
	   MessagePasser mp;
	
	   public MessageListener(Socket socket, MessagePasser mp) {
		   this.socket = socket;
		   this.mp = mp;
	   }
	
	   public void run()
	   {
		   try {
			   while(true)
			   {
				    InputStream inFromServer = socket.getInputStream();
			        
				    ObjectInputStream input = new ObjectInputStream(
	                           inFromServer);
				    
				    TimestampedMessage m = (TimestampedMessage) input.readObject();
				    
				    mp.receiveHandler(m);
			   }   
		   }catch(Exception e)
		   {
			   /* Kill self as socket is broken */
			    this.interrupt();
		   }
	   }
}
