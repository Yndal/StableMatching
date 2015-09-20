package network_flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class NetworkFlow {	
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
		int nodes = Integer.parseInt(scanner.nextLine());
		
		for(int i=0; i<nodes; i++){
			String nodeStr = scanner.nextLine();
			System.out.println("Node: " + nodeStr);
		}
		
		int edges = Integer.parseInt(scanner.nextLine()); 
		
		for(int i=0; i<edges; i++){
			String line = scanner.nextLine();
			String[] split =line.split(" ");
			
			String eStart = split[0];
			String eEnd = split[1];
			int eWeight = Integer.parseInt(split[2]);
			
			System.out.println("Edge: " + eStart + " --> " + eEnd + ": " + eWeight);
		}
		
		scanner.close();
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
