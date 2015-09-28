package network_flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
	private final List<Edge> edges = new ArrayList<>();
	private final List<Node> nodes = new ArrayList<>();
	private final Node source;
	private final Node target;
	private final HashMap<Integer, Edge> compEdges = new HashMap<>();
	
	public Graph(List<Edge> edges, List<Node> nodes, Node source, Node target, boolean createAsResidualGraph){
		this.nodes.addAll(nodes);
		this.source = source;
		this.target = target;
		if(createAsResidualGraph){
			for(Edge e : edges){
				Edge e1 = new Edge(e.getId(), e.getStartNode(), e.getEndNode(), e.getCapacity(), e.getCapacity()); //Normal direction
				Edge e2 = new Edge(e.getId(), e.getEndNode(), e.getStartNode(), 0, 0); //Complementary direction
				this.edges.add(e1); 
				this.edges.add(e2);
				compEdges.put(e1.getId(), e2);
				compEdges.put(e2.getId(), e1);
			}
		} else {
			this.edges.addAll(edges);
		}	
	}
	
	public Node getSource(){
		return source;
	}
	
	public Node getTarget(){
		return target;
	}
	
	public Node getNodeByID(int id){
		return nodes.get(id);
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
	
	public List<Edge> getEdges(){
		return edges;
	}
	
	public void setAllEdgesAsDiscovered(boolean b){
		for(Edge e : edges)
			e.markDiscovered(b);		
	}
	
	public Edge getComplementaryEdgeFrom(int edgeId){
		return compEdges.get(edgeId);
	}
}
