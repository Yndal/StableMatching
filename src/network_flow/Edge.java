package network_flow;

public class Edge {
	private final Node startNode;
	private final Node endNode;
	private final int capacity;
	private int flow;
	private final int id;
	private boolean discovered = false;
	
	public Edge(int id, Node startNode, Node endNode, int flow, int capacity){
		this.id = id;
		this.startNode = startNode;
		this.endNode = endNode;
		this.capacity = capacity;
		this.flow = flow;
	}

	
	public void markDiscovered(boolean b){
		discovered = b;
	}
	
	public boolean isDiscovered(){
		return discovered;
	}
	
	public int getId(){
		return id;
	}
	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}
	
	public int getCapacity(){
		return capacity;
	}

	public int getFlow() {
		return flow;
	}
	
	public void setFlow(int flow){
		this.flow = flow;
		//TODO Update in residual?
	}
	
	/*public Edge getResidualEdge(){
		return residualEdge;
	}*/
}
