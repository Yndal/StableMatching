package network_flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.ListSelectionEvent;


public class NetworkFlow {	
	private static final int INFINITY  = -1;
	private Graph graph;
	private Graph resGraph;
	private HashMap<Node, Integer> indices;
	private HashMap<Integer, Edge> edges = new HashMap<>();


	private void loadInFile(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		int nodeAmount = Integer.parseInt(scanner.nextLine());
		indices = new HashMap<>(nodeAmount);

		Node source = null;
		Node target = null;
		Node resSource = null;
		Node resTarget = null;

		List<Node> nodes = new ArrayList<>();
		for(int i=0; i<nodeAmount; i++){
			String nodeStr = scanner.nextLine();
			Node node = new Node(nodeStr);
			nodes.add(node);
			indices.put(node, i);
			if(nodeStr.equals("ORIGINS")){
				source = node;
				resSource = new Node(node.getName());
			}
			else if(nodeStr.equals("DESTINATIONS")){
				target = node;
				resTarget = new Node(node.getName());
			}
			System.out.println("Node: " + nodeStr);
		}

		List<Node> resNodes = new ArrayList<>();
		for(Node n : nodes)
			resNodes.add(new Node(n.getName()));
		
		if(source == null || target == null){
			scanner.close();
			throw new RuntimeException("No source and/or target defined in input file!");
		}

		int edgeAmount = Integer.parseInt(scanner.nextLine()); 
		List<Edge> edges = new ArrayList<>(edgeAmount);
		List<Edge> resEdges = new ArrayList<>(edgeAmount*2);
		
		for(int i=0; i<edgeAmount; i++){
			String line = scanner.nextLine();
			String[] split =line.split(" ");

			int eStart = Integer.parseInt(split[0]);
			int eEnd = Integer.parseInt(split[1]);
			int eCap = Integer.parseInt(split[2]);

			//Normal graph
			Node startNode = nodes.get(eStart);
			Node endNode = nodes.get(eEnd);
			Edge edge = new Edge(i, startNode, endNode, 0, eCap);
			
			startNode.addEdge(edge);
			//TODO endNode.addEdge(edge);
			edges.add(edge);
			this.edges.put(i, edge);


			//For res graph
			Node resStartNode = resNodes.get(eStart);
			Node resEndNode = resNodes.get(eEnd);
			Edge resEdge = new Edge(i, resStartNode, resEndNode, eCap, eCap);
			Edge resComplEdge = new Edge(i, resEndNode, resStartNode, 0, eCap);
			
			resStartNode.addEdge(resEdge);
			resEndNode.addEdge(resComplEdge);
			resEdges.add(resEdge);
			resEdges.add(resComplEdge);
			
			System.out.println(String.format("Edge (id start, id end, weight): %2d --> %2d: %3d", eStart, eEnd, eCap));
		}
		scanner.close();

		//Create normal graph
		graph = new Graph(edges, nodes, source, target);
		
		//Create res-graph
		resGraph = new Graph(resEdges, resNodes, resSource, resTarget);
		
	}

	public int solve(){
		if(graph == null)
			throw new RuntimeException("Initialize grah before trying to solve!");

		/*Gf ← residual graph.
		WHILE (there exists an augmenting path P in Gf )
		f ← AUGMENT (f, c, P).
		Update Gf.
		RETURN f.*/ 

		int flow = 0;
		List<Edge> path = BFS(resGraph.getSource(), resGraph.getTarget());
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

			path = BFS(resGraph.getSource(), resGraph.getTarget());
			for(int i=0; i<path.size(); i++)
				System.out.println(path.get(i).getStartNode().getName() + " --> " + path.get(i).getEndNode().getName());
		}

		System.out.println("Solved: Max flow is " + flow);
		return flow;
	}





	class BFSObject{
		private final Node node;
		private final BFSObject parent;
		private final Edge edge;

		private BFSObject(Node node, BFSObject parent, Edge edge){
			this.node = node;
			this.parent = parent;
			this.edge = edge;
		}
	}

	private List<Edge> BFS(Node start, Node target){
		Queue<BFSObject> queue = new LinkedList<>();
		queue.add(new BFSObject(start, null, null));
		
		Set<Node> markedNodes = new HashSet<>();
		
		boolean isDone = false;
		while(!queue.isEmpty() && !isDone){
			BFSObject o = queue.remove();
			if(markedNodes.contains(o.node))
					continue;
			
			markedNodes.add(o.node);
			
			for(Edge e : o.node.getEdges()){
				if(e.getFlow() == INFINITY || e.getFlow() > 0){
					Node endNode = e.getEndNode();
					queue.add(new BFSObject(endNode, o, e));
					
					if(e.getEndNode() == target){
						isDone = true;
						break;
					}
				}
			}
		}

		List<Edge> pathInv = new ArrayList<>();
		BFSObject o = (BFSObject) queue.toArray()[queue.size()-1];//peek();//.remove();
		while(o != null){
			if(o.edge != null)
				pathInv.add(o.edge);
			//System.out.println(o.edge.getStartNode().getName() + "==>" + o.edge.getEndNode().getName());

			o = o.parent;
		}

		List<Edge> path = new ArrayList<>();
		for(int i=pathInv.size()-1; 0<=i; i--){
			Edge e = pathInv.get(i);
			System.out.println(e.getStartNode().getName() + "-->" + e.getEndNode().getName());
			path.add(e);
		}





		/*
		 * while Q is not empty:        
13     
14         u = Q.dequeue()
15     
16         for each node n that is adjacent to u:
17             if n.distance == INFINITY:
18                 n.distance = u.distance + 1
19                 n.parent = u
20                 Q.enqueue(n)
		 * 
		 */


		return path;
	}


	private int augment(int flow, /*int capacity,*/ List<Edge> path){

		/*AUGMENT (f, c, P) 
		b ← bottleneck capacity of path P.
		FOREACH edge e ∈ P
		IF (e ∈ E ) f(e) ← f(e) + b.
		ELSE f(eR) ← f(eR) – b.
		RETURN f*/

		int b = findBottleneck(path);
		System.out.println("Bottleneck: " + b);

		for(Edge edge : path){
			if(graph.getEdges().contains(edge)){ 
				edge.setFlow(edge.getFlow() + b);
			} else {
				//Edge complEdge =  graph.getComplementaryEdgeFrom(edge.getId());
				//complEdge.setFlow(complEdge.getFlow() - b);//edge.getResidualEdge().setFlow(edge.getResidualEdge().getFlow() - b);
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

		args = new String[]{"rail.txt"};

		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt")){
					continue;
				}
				nf.loadInFile(file);
				int max = nf.solve();
			}
		} else{
			nf.loadInFile(new File(input + "/" + args[0]));
			int max = nf.solve();
		}

		nf.printSolution();
	}
}
