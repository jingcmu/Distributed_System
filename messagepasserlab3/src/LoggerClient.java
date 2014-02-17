

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

public class LoggerClient {

	static MessagePasser mp = null;
	static ClockService clock = null;
	static ArrayList<TimestampedMessage> receivedMessages = new ArrayList<TimestampedMessage>();
	
	public MessagePasser getMp() {
		return mp;
	}
	
	

	
	public static void main(String[] args) {

		String configFilePath = ""; 
		String processName = "logger";
		String clockType = "";
			
		
		try {
			configFilePath = args[0];
			clockType = args[1];
			
			System.out.println("\nConfig File Path : " + configFilePath
					+ "\nClock Type : " + clockType);

			initClock(configFilePath, processName, clockType);
			MessagePasser mp = new MessagePasser(configFilePath, processName, clock);

			startLoggerReceiver(mp, receivedMessages);
			
			startDialogue(mp);

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out
					.println("Invalid args : Expecting LoggerClient configFilePath clockType");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception : " + e.getMessage());
		}
	}


	private static void startDialogue(MessagePasser mp) throws Exception {
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);

		while (true) {
			System.out.println("\nPress any key Log Messages ");
			reader.nextLine();
			
			System.out.println("Log messages : ");
			printLogMessages();
			
		}
	}


	private static void printLogMessages() {
		TimestampedMessage prev = null;
		synchronized (receivedMessages) {
			
			Collections.sort(receivedMessages);

			for (TimestampedMessage msg : receivedMessages) {
				if (!msg.isConcurrent(prev)) {
					System.out.println();
				}
				System.out.println(msg.toString());
				prev = msg;
			}
			
		}
		
	}

	private static void initClock(String configFilePath, String processName,
			String clockType) throws Exception {
		
		if(clockType.equals("Logical"))
	    {
	    	clock = new LogicalClock();
	    }
	    
	    else if(clockType.equals("Vector"))
	    {
	    	int [] config;
	    	
	    	config = readConfiguration(configFilePath, processName);
	    	Integer nProcesses = config[0];
	    	Integer myIndex = config[1];
	    	System.out.println("Config says : nProcesses : " + config[0] + " myIndex = " + config[1]);   	
	    	if (myIndex == -1) {
	    		throw new Exception("Config for "+ processName +" not found!");
	    	}
	    	clock = new VectorClock(nProcesses, myIndex);
	    }
	    else {
	    	throw new Exception("Invalid clocktype selected!");
	    }
		
	}
	
	private static int [] readConfiguration(String FilePath, String processName)
			throws Exception {

		try {
			int [] config = new int[2];
			
			int nProcesses = 0;
			int myIndex = 0;

			
			System.out.println("\nReading the config file");
			File file = new File(FilePath);
			InputStream input = new FileInputStream(file);

			Yaml yaml = new Yaml();
			Map<String, Object> data = (Map<String, Object>) yaml.load(input);
			ArrayList<Map> peers = null;
			
			peers = (ArrayList<Map>) data.get("configuration");
			nProcesses = peers.size();
			
			for (Map peer : peers) {
				String name = peer.get("name").toString();
				if (name.equals(processName)) {
					break;
				}
				myIndex++;
				
			}
			
			input.close();
			
			
			config[0] = nProcesses;
			if (myIndex == nProcesses) {
				config[1] = -1;
			}
			else {
				config[1] = myIndex;
			}
			
			return config;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private static void startLoggerReceiver(MessagePasser mp, ArrayList<TimestampedMessage> receivedMessages) throws Exception {
		/*
		 * create thread that keeps on receiving messages from MessagePasser
		 */
		try {
			Thread t = new LoggerReceiver(mp, receivedMessages);
			t.start();
		} catch (Exception e) {
			throw new Exception("Unable to create thread to listen for log messages.");
		}
	}
}