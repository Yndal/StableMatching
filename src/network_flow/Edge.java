package network_flow;

public class Edge {
	private final Node startNode;
	private final Node endNode;
	private int capacity;
	private int flow;
	private final int id;
	private boolean discovered = false;
	private final boolean isForward;
	private Edge reverseEdge;
	
	public Edge(int id, Node startNode, Node endNode, int flow, int capacity, boolean isForward){
		this.id = id;
		this.startNode = startNode;
		this.endNode = endNode;
		this.capacity = capacity;
		this.flow = flow;
		this.isForward = isForward;
	}

	public void setReverseEdge(Edge revEdge){
		this.reverseEdge = revEdge;
	}
	
	public Edge getReverseEdge(){
		return reverseEdge;
	}
	
	public int getFreeCapacity(){
		if(capacity == NetworkFlow.INFINITY) 
			return NetworkFlow.INFINITY;
		
		return capacity - flow + reverseEdge.getFlow();
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
	
	public void augmentFlow(int flow){
		this.flow += flow;
		if(this.reverseEdge != null)
			this.reverseEdge.capacity += flow;
	}
	
	public boolean isForward(){
		return isForward;
	}
	
	/*public Edge getResidualEdge(){
		return residualEdge;
	}*/
}
