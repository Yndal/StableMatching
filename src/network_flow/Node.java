package network_flow;

import java.util.HashSet;
import java.util.Set;

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

	public String getName(){
		return name;
	}
}
