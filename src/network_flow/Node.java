package network_flow;

import java.util.HashSet;
import java.util.Set;

class NodeEdgePair {
	public final Edge edge;
	public final Node node;

	public NodeEdgePair(Edge e, Node n) {
		this.edge = e;
		this.node = n;
	}
}

public class Node {
	private final Set<Edge> edges = new HashSet<>();
	private final String name;
	
	public Node(String name){
		this.name = name;
	}
	
	public boolean addEdge(Edge edge){
		return edges.add(edge);
	}
	
	public Set<Edge> getEdges(){
		return edges;
	}

	public Set<NodeEdgePair> getAdjecents(){
		Set<Edge> edges = getEdges();
		Set<NodeEdgePair> nodes = new HashSet<>();

		for (Edge e : edges) {
			Node start = e.getStartNode();
			Node end = e.getEndNode();
			Node other = start == this ? end : start;
			nodes.add(new NodeEdgePair(e, other));
		}
		return nodes;
	}

	public String getName(){
		return name;
	}
}
