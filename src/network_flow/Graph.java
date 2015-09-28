package network_flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
	private final List<Edge> edges = new ArrayList<>();
	private final List<Node> nodes = new ArrayList<>();
	private final Node source;
	private final Node target;
	
	public Graph(List<Edge> edges, List<Node> nodes, Node source, Node target){
		this.edges.addAll(edges);
		this.nodes.addAll(nodes);
		this.source = source;
		this.target = target;	
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
}
