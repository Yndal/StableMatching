package network_flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkFlow {	
	private Graph graph;
	
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
	
	private void loadInFile(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		int nodeAmount = Integer.parseInt(scanner.nextLine());
		List<String> indices = new ArrayList<>(nodeAmount);
		
		Node source = null;
		Node target = null;
		
		List<Node> nodes = new ArrayList<>();
		for(int i=0; i<nodeAmount; i++){
			String nodeStr = scanner.nextLine();
			Node node = new Node(nodeStr);
			nodes.add(node);
			indices.add(nodeStr);
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
		
		for(int i=0; i<edgeAmount; i++){
			String line = scanner.nextLine();
			String[] split =line.split(" ");
			
			int eStart = Integer.parseInt(split[0]);
			int eEnd = Integer.parseInt(split[1]);
			int eWeight = Integer.parseInt(split[2]);
			
			Node startNode = nodes.get(eStart);
			Node endNode = nodes.get(eEnd);
			Edge edge = new Edge(startNode, endNode, eWeight);
			
			startNode.addEdge(edge);
			endNode.addEdge(edge); //Edges are undirected
			edges.add(edge);
			
			System.out.println(String.format("Edge (id start, id end, weight): %2d --> %2d: %3d", eStart, eEnd, eWeight));
		}
		scanner.close();
		
		graph = new Graph(edges, nodes, source, target);	
	}

	public void solve(){
		//TODO
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
				nf.loadInFile(file);
				nf.solve();
			}
		} else{
			nf.loadInFile(new File(input + "/" + args[0]));
			nf.solve();
		}
		
		nf.printSolution();
	}
}
