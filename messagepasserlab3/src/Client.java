
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

public class Client {

	private static MessagePasser mp = null;
	static ClockService clock = null;
	static TimestampedMessage tsm = null;
	static MulticastService mcs = null;
	static MutualExclusionService mutexService = null;
	static MessageShower M = null;
	
	public static MessagePasser getMp() {
		return mp;
	}
	
	public static MulticastService getMcs() {
		return mcs;
	}
	
	public static ClockService getClock() {
		return clock;
	}
	
	public static MutualExclusionService getMetux() {
		return mutexService;
	}
	
	public static MessageShower getMessageShower() {
		return M;
	}
	
	public static void setMp(MessagePasser MP) {
		mp = MP;
	}
	
	public static void setMcs(MulticastService MCS) {
		mcs = MCS;
	}
	
	public static void setClock(ClockService Clock) {
		clock = Clock;
	}
	
	public static void setMetux(MutualExclusionService MutexService) {
		mutexService = MutexService;
	}
	
	public static void setMessageShower(MessageShower MS) {
		M = MS;
	}

	public static void main(String[] args) {
		M = new MessageShower(args[0], args[1]);
	}

	public static void initClock(String configFilePath, String processName,
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
	    	
	    	if (myIndex == -1) {
	    		throw new Exception("Config for "+ processName +" not found!");
	    	}
	    	clock = new VectorClock(nProcesses, myIndex);
	    }
	    else {
	    	throw new Exception("Invalid clocktype selected!");
	    }
		
	}
	
	/*
	 * Read number of processes and self index for vector clock.
	 */
	private static int [] readConfiguration(String FilePath, String processName)
			throws Exception {

		try {
			int [] config = new int[2];
			
			int nProcesses = 0;
			int myIndex = 0;

			
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
	
}