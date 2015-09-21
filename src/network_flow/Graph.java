package network_flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
	static class ResidualGraph extends Graph{
		private final HashMap<Edge, Edge> resEdges = new HashMap<>();
		
		public ResidualGraph(List<Edge> edges, List<Node> nodes, Node source, Node target) {
			super(edges, nodes, source, target);
			
			for(Edge edge : edges){
				resEdges.put(edge, new Edge(edge.getEndNode(), edge.getStartNode(), edge.getCapacity(), edge.getCapacity()));
			}
		}
		
		public Edge getResEdge(Edge edge){
			return resEdges.get(edge);
		}
	}
	
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
	
	public void setAllEdgesAsDiscovered(boolean b){
		for(Edge e : edges)
			e.markDiscovered(b);		
	}
}
