package network_flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
	static class ResidualGraph extends Graph{
		private final HashMap<Edge, Edge> resEdges = new HashMap<>();
		
		public ResidualGraph(List<Edge> edges, List<Node> nodes, Node source, Node target, HashMap<Edge,Edge> compEdges) {
			super(edges, nodes, source, target, false);
			
			for(Edge edge : edges){
				Edge resEdge = new Edge(edge.getEndNode(), edge.getStartNode(), edge.getCapacity(), edge.getCapacity());
				compEdges.put(edge, resEdge);
				compEdges.put(resEdge, edge);
				resEdges.put(edge, resEdge);
			}
		}
	}
	
	private final List<Edge> edges = new ArrayList<>();
	private final List<Node> nodes = new ArrayList<>();
	private final Node source;
	private final Node target;
	private final ResidualGraph resGraph;
	private final HashMap<Edge, Edge> compEdges = new HashMap<>();
	
	public Graph(List<Edge> edges, List<Node> nodes, Node source, Node target, boolean createWithResidualGraph){
		this.edges.addAll(edges);
		this.nodes.addAll(nodes);
		this.source = source;
		this.target = target;
		if(createWithResidualGraph){
			resGraph = new ResidualGraph(edges, nodes, source, target, compEdges);
		} else 
			resGraph = null;
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
	
	public ResidualGraph getResidualGraph(){
		return resGraph;
	}
	
	public Edge getComplementaryEdgeFrom(Edge edge){
		return compEdges.get(edge);
	}
}
