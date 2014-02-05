package Lab0;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;


public class ConfigHandler {
	
	public static List<Rule> sendRules = new ArrayList<Rule>();

	public static List<Rule> receiveRules = new ArrayList<Rule>();
	
	@SuppressWarnings("unchecked")
	public static void config(String filename, String local_name, MessagePasser messagePasser) throws UnknownHostException,
			IOException {
		InputStream input = new FileInputStream(new File(filename));
		Yaml yaml = new Yaml();
		HashMap<String, Object> data = (HashMap<String, Object>) yaml
				.load(input);
		
		sendRules.clear();
		receiveRules.clear();

		int id = 0;
		List<HashMap<String, Object>> nodes = (List<HashMap<String, Object>>) data
				.get("configuration");
		for (HashMap<String, Object> node : nodes) {
			Node newNode = new Node();
			newNode.name = (String) node.get("name");
			newNode.ip = (String) node.get("ip");
			newNode.port = (Integer) node.get("port");

			messagePasser.nodes.put(newNode.name, newNode);
			if (newNode.name.equals(local_name)) {
				messagePasser.id = id;
			}
			id++;
			//System.out.println("New node: " + newNode.name);

		}

		List<HashMap<String, Object>> sendRules = (List<HashMap<String, Object>>) data
				.get("sendRules");
		for (HashMap<String, Object> rule : sendRules) {
			Rule newRule = new Rule();
			for (String key : rule.keySet()) {
				if (key.equals("src")) {
					newRule.src = (String) rule.get("src");
				} else if (key.equals("dest")) {
					newRule.dest = (String) rule.get("dest");
				} else if (key.equals("kind")) {
					newRule.kind = (String) rule.get("kind");
				} else if (key.equals("seqNum")) {
					newRule.seq = (Integer) rule.get("seqNum");
				} else if (key.equals("action")) {
					newRule.action = Action.valueOf(((String) rule
							.get("action")).toUpperCase());
				} else if (key.equals("dupe")) {
					newRule.dupe = (Boolean) rule.get("dupe");
				} 
			}

			ConfigHandler.sendRules.add(newRule);

			//System.out.println("New send rule: " + newRule.action);

		}

		List<HashMap<String, Object>> receiveRules = (List<HashMap<String, Object>>) data
				.get("receiveRules");
		for (HashMap<String, Object> rule : receiveRules) {
			Rule newRule = new Rule();
			for (String key : rule.keySet()) {
				if (key.equals("src")) {
					newRule.src = (String) rule.get("src");
				} else if (key.equals("dest")) {
					newRule.dest = (String) rule.get("dest");
				} else if (key.equals("kind")) {
					newRule.kind = (String) rule.get("kind");
				} else if (key.equals("seqNum")) {
					newRule.seq = (Integer) rule.get("seqNum");
				} else if (key.equals("action")) {
					newRule.action = Action.valueOf(((String) rule
							.get("action")).toUpperCase());
				} else if (key.equals("dupe")) {
					newRule.dupe = (Boolean) rule.get("dupe");
				} 
			}
			ConfigHandler.receiveRules.add(newRule);

			//System.out.println("New receive rule: " + newRule.action);

		}
	}

}
