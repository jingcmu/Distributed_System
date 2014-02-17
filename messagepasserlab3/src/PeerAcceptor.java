
import java.net.*;	
import java.io.*;


public class PeerAcceptor extends Thread
{
   private ServerSocket serverSocket;
   private MessagePasser mp;
   
   public PeerAcceptor(MessagePasser mp) throws IOException
   {
	  InetAddress addr = InetAddress.getByName(mp.getIp());
      this.serverSocket = new ServerSocket(mp.getPort(), 0, addr);
      this.mp = mp;
   }

   public void run()
   {
      while(true)
      {
         try
         {
            Socket socket = serverSocket.accept();
           Thread t = new MessageListener(socket, this.mp);
    		t.start();
    		
            
         }catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }
}