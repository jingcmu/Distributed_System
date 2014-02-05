package Lab1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import Lab0.Listener;
import Lab0.Message;
import Lab0.MessagePasser;

public class Logger
{
	private static final String logFile = "log/logged.txt";
	
	/* The foll. variables are accessed by the inner classes: LogHandler and QueueAdder */
	private Socket socket=null;
	private static String processName =null;
	private MessagePasser mp =null;
	
	public static void main(String[] args) throws IOException
	{
		/* args[0]-- config file
		 * args[1]-- process name
		 *  */
		MessagePasser mp = new MessagePasser(args[0], args[1], args[2]);//I havent checked your MessagePasser parameters. So please correct
		//mp.init(); //Same here. Didnt check your method. Please call the appropriate one
		
		Listener listener = new Listener();
		listener.start();
		
		/*LogHandler handler = new LogHandler();
		processName = args[1];
		handler.start(); // Starts the LogHandler written at the bottom of this class*/

		String input = null;
		BufferedReader br = null;
		

		try{
			br = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				/* Take input from user */
				System.out.println("Please enter the input: quit or log");
				input = br.readLine();
				if( input != null && (input.equals("quit")) )
					System.exit(1);
				
				if(input != null && (input.equals("log")) )
				{
					 /* You are returning Message object. Instead, if you can add those messages to an ArrayList 
					  * and return it, the following code works perfect for sorting those messages. Else, you can
					  * change this implementation:
					  */
						ArrayList<TimeStampedMessage> logInfo = new ArrayList<TimeStampedMessage>();
						for (Message message : MessagePasser.deliverQueue) {
							logInfo.add((TimeStampedMessage)message);
						};
						
						if (args[2].equals("logical")) {
							logLogical(logInfo);
						} else if (args[2].equals("vector")) {
							logVector(logInfo);
						}

						
				}
				else
					System.out.println("Enter log or quit\n");
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			try{
				if(br != null)
					br.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

	private static void logVector(ArrayList<TimeStampedMessage> logInfo) {
		Collections.sort(logInfo);
		
		for(int i = 0; i < logInfo.size(); i++) {
			System.out.println(i + ": " + logInfo.get(i));
		}
		
		for (int i = 0; i < logInfo.size(); i++) {
			System.out.print("relations for message " + i + ": ");
			for (int j = 0; j < logInfo.size(); j++) {
				if (i != j) {
					int compare = logInfo.get(i).compareTo(logInfo.get(j));
					boolean equal = logInfo.get(i).equal(logInfo.get(j));
					if (compare == -1) {
						System.out.print("message " + i + " < message" + j + " ");
					} else if (compare == 1) {
						System.out.print("message " + i + " > message" + j + " ");
					} else if (equal == true) {
						System.out.print("message " + i + " = message" + j + " ");
					} else {
						System.out.print("message " + i + " || message" + j + " ");
					}
				}
			}
			System.out.println();
		}
		
	}

	private static void logLogical(ArrayList<TimeStampedMessage> logInfo) {
		Collections.sort(logInfo);
		
		for(int i = 0; i < logInfo.size(); i++) {
			System.out.println(i + ": " + logInfo.get(i));
		}
		
	}
	
}

