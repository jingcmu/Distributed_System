import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class MulticastService {

	ArrayList<MulticastGroup> groups;
	MessagePasser mp = null; 
	String processName = null;
	private MutualExclusionService mutexService = null;
	

	public MulticastService(String configFilePath, String processName,
			MessagePasser mp) throws Exception {
		try {
			this.groups = new ArrayList<MulticastGroup>();
			this.mp = mp;
			this.processName = processName;
			
			parseConfiguration(configFilePath);
			
			startNACKSender();
			
		}
		catch(Exception e) {
			Client.getMessageShower().setJLabelStatus("Error!");
			Client.getMessageShower().setJTextStatus("Error instantiating Multicast Service\n");
			e.printStackTrace();
			throw e;
		}
	}

	private void startNACKSender() throws Exception {
		try {
			//Thread t = new NACKSender(this, 10000L);
			//t.start();
		} catch (Exception io) {
			io.printStackTrace();
			throw new Exception("Unable to start NACKSender.");
		}

	}

	public ArrayList<MulticastGroup> getGroups() {
		return groups;
	}

	private void parseConfiguration(String FilePath) throws Exception {

		try {
			Client.getMessageShower().setJTextStatus("MC : Parsing the config file for groups...\n");
			File file = new File(FilePath);
			InputStream input = new FileInputStream(file);

			Yaml yaml = new Yaml();
			ArrayList<Map> mapElements = null;
			
			Map<String, Object> data = (Map<String, Object>) yaml.load(input);
			mapElements = (ArrayList<Map>) data.get("groups");
			
			createGroups(mapElements);
			
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createGroups(ArrayList<Map> mapElements) throws Exception {
		try {
			/* Display nodes in config file */
			StringBuffer str = new StringBuffer();
			for (Map element : mapElements) {
				String name;
				
				name = element.get("name").toString();
				str.append(name + "  ");
				
				MulticastGroup g = new MulticastGroup(name, mp, this);
					this.groups.add(g);
				
				ArrayList<String> members = (ArrayList<String>) element.get("members");
				
				boolean isMember = false;
				
				for (String member : members) {
					g.addMember(member);
					
					if (member.equals(processName)) {
						isMember = true;
					}
				}

				if (isMember == false) {
					groups.remove(g);
					str.append("member = NO \n");
				}
				else {
					
					int nMembers = g.getGroupSize();
					int myIndex = g.getIndex(processName);
					str.append("member = YES \n");
					
					VectorClock clock = new VectorClock(nMembers, myIndex);
					g.setClock(clock);
				}
				//Client.getMessageShower().setJTextGroupConfig(str.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void printGroups() {
		if (groups == null) {
			Client.getMessageShower().setJLabelStatus("No groups present!");
			return;
		}
		for (MulticastGroup g : groups) {
			g.print();
		}
	}

	public void rMulticast(String groupName, Message m) {
		/* Argument validation */
		if (this.mp == null) {
			Client.getMessageShower().setJLabelStatus("Multicast failed!");
			Client.getMessageShower().setJTextStatus("Message Passer not yet initialized.\n");
			return;
		}
		
		MulticastGroup g = findGroup(groupName);
		if (g == null) {
			Client.getMessageShower().setJLabelStatus("Multicast failed!");
			Client.getMessageShower().setJTextStatus("Current process " + processName 
					+ " is not a part of multicast group [" + groupName + "]");
			return;
		}
		
		MulticastMessage msg = null;
		try {
			if (MulticastMessage.class.isInstance(m)) {
				msg = new MulticastMessage((MulticastMessage)m);
			}
			else {
				msg = new MulticastMessage(m, g, this.processName);
				Client.getMessageShower().setJTextStatus("  Outgoing multicast message : \n");
				msg.printHeaders();
			}
		} catch (Exception e) {
			Client.getMessageShower().setJTextStatus("Exception : " + e.toString() + "\n");
			e.printStackTrace();
			return;
		}
		
		ArrayList<String> members = g.getMembers();
		multicast(members, msg);
		
		return;
	}
	
	public void multicast(ArrayList<String> recepients, Message msg) {
		
		for (String member : recepients) {
			Client.getMessageShower().setJTextStatus(
					"Sending multicast message to " + member + "\n");
				
			msg.setDestination(member);
			
			boolean ret = mp.send(msg);
			if (ret == false) {
				Client.getMessageShower().setJLabelStatus(
						"Error in sending message!");
				Client.getMessageShower().setJTextStatus(
						"Error sending message to " + member + "\n");
			}
		}
		
	}
	
	public void bDeliver(MulticastMessage m) {
		
		// Add to holdBack queue, and check if r-delivery possible
		ArrayList<String> pendingRecepients = processReceivedMulticastMessage(m);
		printHoldBackQueues();
		
		// message received from all members of group.
		if (pendingRecepients == null) {
			return;
		}
		
		// If receiver != sender, broadcast to all.
		if (m.getSource().equals(processName)) {
			return;
		}
		// PAUSE A SECOND!!!  
		//Scanner reader = new Scanner(System.in);
		//reader.nextLine();
		
		multicast(pendingRecepients, m);
	}
	

	private void printHoldBackQueues() {
		Client.getMessageShower().setJTextStatus("\nHoldBack Queues are : \n");
		for (MulticastGroup g : groups) {
			g.printHoldBackQueue();
		}

	}

	private MulticastGroup findGroup(String groupName) {
		
		for (MulticastGroup g : groups) {
			if (g.getGroupName().equals(groupName)) {
				return g;
			}
		}
		return null;
	}
	
	private ArrayList<String> processReceivedMulticastMessage(MulticastMessage m) {
		MulticastGroup g = findGroup(m.getGroupName());
		
		if (g == null) {
			Client.getMessageShower().setJLabelStatus(
					"Received message for group of which " + this.processName + "is not a member.\n");
			return null;
		}
		
		return g.processIncomingMessage(m);
	}

	public void setMutexService(MutualExclusionService mutexService) {
		this.mutexService = mutexService;
		
	}

	protected synchronized MutualExclusionService getMutexService() {
		return mutexService;
	}

}
