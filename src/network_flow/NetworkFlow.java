package network_flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.event.ListSelectionEvent;

import network_flow.Graph.ResidualGraph;

public class NetworkFlow {	
	private static final int INFINITY  = -1;
	private Graph graph;
	private ResidualGraph resGraph;
	private HashMap<Node, Integer> indices;
	
	private void loadOutFile(File file) throws FileNotFoundException{
		/*Scanner scanner = new Scanner(file);
		String s;
		boolean hasLetters = false;
		while(scanner.hasNextLine()){
			s = scanner.nextLine();
			if (s.startsWith("#"))
				continue;
			//Letters
			if (!hasLetters){
				for (String letter : s.trim().split("\\s+")){
					letters.add(letter);
					alignmentData.put(letter, new HashMap<String, Integer>());
				}
				hasLetters = true;
			}
			//Costs
			else {
				String[] splits = s.trim().split("\\s+");
				if (splits.length == letters.size() + 1){
					String letter = splits[0];
					for (int i=1; i<splits.length; i++){
						String colLetter = letters.get(i-1);
						int cost = Integer.parseInt(splits[i]);
						alignmentData.get(letter).put(colLetter, cost);
					}
				}
			}
		}
		scanner.close();*/
	}
	
	private int loadInFile(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		int nodeAmount = Integer.parseInt(scanner.nextLine());
		indices = new HashMap<>(nodeAmount);
		
		Node source = null;
		Node target = null;
		
		List<Node> nodes = new ArrayList<>();
		for(int i=0; i<nodeAmount; i++){
			String nodeStr = scanner.nextLine();
			Node node = new Node(nodeStr);
			nodes.add(node);
			indices.put(node, i);
			if(nodeStr.equals("ORIGINS"))
				source = node;
			else if(nodeStr.equals("DESTINATIONS"))
				target = node;
			System.out.println("Node: " + nodeStr);
		}
		
		if(source == null || target == null){
			scanner.close();
			throw new RuntimeException("No source and/or target defined in input file!");
		}
		
		int edgeAmount = Integer.parseInt(scanner.nextLine()); 
		List<Edge> edges = new ArrayList<>(edgeAmount);
		int maxCapacity = 0;
		
		for(int i=0; i<edgeAmount; i++){
			String line = scanner.nextLine();
			String[] split =line.split(" ");
			
			int eStart = Integer.parseInt(split[0]);
			int eEnd = Integer.parseInt(split[1]);
			int eWeight = Integer.parseInt(split[2]);
			
			if(eWeight < maxCapacity) //Store max capacity
				maxCapacity = eWeight;
			
			Node startNode = nodes.get(eStart);
			Node endNode = nodes.get(eEnd);
			Edge edge = new Edge(startNode, endNode, 0, eWeight);
			
			startNode.addEdge(edge);
			endNode.addEdge(edge); //Edges are undirected
			edges.add(edge);
			
			System.out.println(String.format("Edge (id start, id end, weight): %2d --> %2d: %3d", eStart, eEnd, eWeight));
		}
		scanner.close();
		
		graph = new Graph(edges, nodes, source, target);
		resGraph = new ResidualGraph(edges, nodes, source, target);
		
		return maxCapacity;
	}

	public int solve(int maxCapacity){
		if(graph == null)
			throw new RuntimeException("Initialize grah before trying to solve!");
		
		/*Gf ← residual graph.
		WHILE (there exists an augmenting path P in Gf )
		f ← AUGMENT (f, c, P).
		Update Gf.
		RETURN f.*/ 
		
		int flow = 0;
		List<Edge> path = getPath(resGraph, resGraph.getSource(), resGraph.getTarget());
		for(int i=0; i<path.size(); i++){
			int startId = indices.get(path.get(i).getStartNode());
			int endId = indices.get(path.get(i).getEndNode());
			System.out.println(startId + " --> " + endId);
		}
		
		while(!path.isEmpty()){
			int aug = augment(flow, /*capacity,*/ path);
			if(aug == INFINITY)
				throw new RuntimeException("Path augmented with INFINITY");
			else
				flow += aug;
			
			path = getPath(resGraph, resGraph.getSource(), resGraph.getTarget());
			for(int i=0; i<path.size(); i++)
				System.out.println(path.get(i).getStartNode().getName() + " --> " + path.get(i).getEndNode().getName());
		}
		
		return flow;
	}
	
	private List<Edge> BFS(Node start, Node target){
		for(Edge e : start.getEdges()){
			if(e.isDiscovered() || e.getFlow() == 0)
				continue;
			
			e.markDiscovered(true);
			if(e.getEndNode() == target){
				List<Edge> list = new ArrayList<>();
				list.add(0,e);
				return list;
			} else {
				List<Edge> list = BFS(e.getEndNode(), target);
				if(!list.isEmpty()){
					list.add(0,e);
					return list;
				} 
			}	
		}
		
		return new ArrayList<>();
	}
	
	private List<Edge> getPath(Graph graph, Node source, Node target){
		graph.setAllEdgesAsDiscovered(false);
		
		return BFS(source, target);
		
		/*Queue<Edge> pathStack = new LinkedList<>();
		
		for(Edge edge: source.getEdges()){
			pathStack.add(edge);
			
			while(!pathStack.isEmpty()){
				Edge e = pathStack.remove();
				if(e.getEndNode() == target) {
					break; //Path found
				} else {
					for(Edge e : e.getEndNode().getEdges()){
						
					}
				}
			}
		}
		
		
		
		
		
		return path;*/
	}
	
	
	private int augment(int flow, /*int capacity,*/ List<Edge> path){
	
		/*AUGMENT (f, c, P) 
		b ← bottleneck capacity of path P.
		FOREACH edge e ∈ P
		IF (e ∈ E ) f(e) ← f(e) + b.
		ELSE f(eR) ← f(eR) – b.
		RETURN f*/
		
		int b = findBottleneck(path);
		
		for(Edge edge : path){
			if(graph.getEdges().contains(edge)){ //TODO Flaw here
				edge.setFlow(edge.getFlow() + b);
			} else {
				edge.getResidualEdge().setFlow(edge.getResidualEdge().getFlow() - b);
			}
		}
		
		return flow + b; //Ved ikke om flow skal forøges med b, men det er et godt bud!
	}
	
	private int findBottleneck(List<Edge> path){
		int min = Integer.MAX_VALUE; //Better than INFITY
		for(Edge edge : path){
			if(edge.getCapacity() == INFINITY)
				continue;
			
			int capLeft = edge.getCapacity() - edge.getFlow();
			
			if(capLeft < min)
				min = capLeft;
		}
		
		if(min == Integer.MAX_VALUE)
			min = INFINITY;
		return min;
	}
	
	private void printSolution() {
		//TODO
	}

	public static void main(String[] args) throws Exception {
		String input = "input/network_flow";
		NetworkFlow nf = new NetworkFlow();
		nf.loadOutFile(new File(input + "/" + "BLOSUM62.txt"));
		
		args = new String[]{"rail.txt"};
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt")){
					continue;
				}
				int max = nf.loadInFile(file);
				nf.solve(max);
			}
		} else{
			int max = nf.loadInFile(new File(input + "/" + args[0]));
			nf.solve(max);
		}
		
		nf.printSolution();
	}
}
