import java.util.ArrayList;
import java.util.List;


public class Group {		
	private String name;		
	private List<String> members;
	private List<Node> nodes;
	private VectorTimeStamp timestamp;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	public List<Node> getNodes() {
		return this.nodes;
	}
	
	public void setNodes(List<Node> configuration) {
		this.nodes = new ArrayList<Node>();
		for(Node node : configuration){
			if(members.contains(node.getName())) {
				this.nodes.add(node);
			}
		}
		
 	}

	public VectorTimeStamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(VectorTimeStamp timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
