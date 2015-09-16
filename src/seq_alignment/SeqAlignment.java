package seq_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SeqAlignment {
	private class FastaRecord{
		private final String name;
		private final String sequence;
		private String alignedSeq;

		private FastaRecord(String name, String sequence){
			this.name = name;
			this.sequence = sequence;
		}

		public String getName(){
			return name;
		}

		public String getSeq() {
			return sequence;
		}
		
		public String getAlignedSeq() {
			return alignedSeq;
		}

		public void setAlignedSeq(String alignedSeq) {
			this.alignedSeq = alignedSeq;
		}
	}
	
	private class Result{
		private int cost;
		private FastaRecord record1;
		private FastaRecord record2;
		
		public Result(int cost, FastaRecord record1, FastaRecord record2){
			this.cost = cost;
			this.record1 = record1;
			this.record2 = record2;
		}
		
		public int getCost(){
			return cost;
		}
		
		public FastaRecord getRecord1(){
			return record1;
		}
		
		public FastaRecord getRecord2(){
			return record2;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(record1.name + "--" + record2.name + ": " + cost + "\n");
			sb.append(record1.getAlignedSeq() + "\n");
			sb.append(record2.getAlignedSeq() + "\n");
			
			return sb.toString();
		}
	}

	private static final String letter_Gap = "*";
	private static int deltaValue = -4;
	private List<String> letters = new ArrayList<>();
	private HashMap<String, HashMap<String, Integer>> alignmentData = new HashMap<>();
	private List<FastaRecord> fastaRecords = new ArrayList<>();
	private List<Result> results = new ArrayList<>();
	
	public SeqAlignment(File blosumFile) throws FileNotFoundException{
		loadBlosum62(blosumFile);
	}
	
	private int getCost(char c1, char c2){
		return getCost(c1 + "", c2 + "");
	}
	
	private int getCost(String letter1, String letter2){
		return alignmentData.get(letter1).get(letter2);
	}
	
	private void loadBlosum62(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
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
		scanner.close();
	}
	
	private void loadFasta(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		String s, name=null, seq=null;
		while(scanner.hasNextLine()){
			s = scanner.nextLine();
			if (s.contains(">")){
				if (name != null && !name.isEmpty()){
					fastaRecords.add(new FastaRecord(name, seq));
				}
				name = s.substring(1).trim();
				seq = "";
			}
			else if(!s.trim().isEmpty()){
				seq += s.trim();
			}
		}
		if(name != null & !name.isEmpty()){
			fastaRecords.add(new FastaRecord(name, seq));
		}
		scanner.close();
	}

	public void align(){
		for (int i=0; i<fastaRecords.size()-1; i++){
			for (int j=i+1; j<fastaRecords.size(); j++){
				int cost = align(fastaRecords.get(i), fastaRecords.get(j));
				results.add(new Result(cost, fastaRecords.get(i), fastaRecords.get(j)));
			}
		}
	}
	
	//This method might not be necessary :)
	private int align(FastaRecord rec1, FastaRecord rec2){
		String seq1 = rec1.sequence;
		String seq2 = rec2.sequence;
		
		int m = seq1.length();
		int n = seq2.length();
		int[][] M = new int[m+1][n+1];
		
		for(int i=0; i<m+1; i++)
			M[i][0] = deltaValue*i;
		for(int i=0; i<n+1; i++)
			M[0][i] = deltaValue*i;
		
		//Create cost matrix
		for(int iM=1; iM<m+1; iM++){
			for(int iN=1; iN<n+1; iN++){
				int cost1 = M[iM-1][iN-1] + getCost(seq1.charAt(iM-1), seq2.charAt(iN-1));
				int cost2 = M[iM-1][iN] + getCost(seq1.charAt(iM-1) + "", letter_Gap);
				int cost3 = M[iM][iN-1] + getCost(letter_Gap, seq2.charAt(iN-1) + "");
				
				
				M[iM][iN] = Math.max(cost1, 
						Math.max(cost2, 
								cost3));
			}
		}
		
//		System.out.println(seq1);
//		System.out.println(seq2);
//		printArray(M);
		
		//Get optimal solution
		String s1 = "";
		String s2 = "";
		int totalCost = M[m][n];
		for(int im=seq1.length(); im>0;){
			for(int in=seq2.length(); in>0;){				
				
				//Case 1: Char from seq1
				int cost1 = deltaValue + M[im-1][in];
				
				//Case 2: Char from seq2
				int cost2 = deltaValue + M[im][in-1];
				
				//Case 3: Char from seq1 and seq2
				int cost3 = M[im][in] + M[im-1][in-1];
				
				int max = Math.max(cost1, Math.max(cost2, cost3));
				
				if(cost1 == max){
					s1 = seq1.charAt(im-1) + s1;
					s2 = letter_Gap + s2;	
					im--;
					
				} else if(cost2 == max){
					s1 = letter_Gap + s1;
					s2 = seq2.charAt(in-1) + s2;
					in--;
				} else if (cost3 == max){
					s1 = seq1.charAt(im-1) + s1;
					s2 = seq2.charAt(in-1) + s2;
					im--;
					in--;
				}
//				System.out.println(String.format("s1: %10s ", s1));
//				System.out.println(String.format("s2: %10s ", s2));
//				System.out.println();
				
				
			}
		}
		
		rec1.setAlignedSeq(s1);
		rec2.setAlignedSeq(s2);
		
		System.out.println(s1);
		System.out.println(s2);
		
		return totalCost;
	}
	
	
	private void printArray(int[][] M) {
		for(int m=M.length-1; 0<=m; m--){
			for(int n=0; n<M[0].length;n++){
				System.out.print(String.format("%3s\t", M[m][n]));
			}
			System.out.println();
			
		}
	}

	private String lastLetter(String str){
		return str.substring(str.length()-1);
	}
	
	public void printResult(){
		for(Result result : results){
			System.out.println(result);	
		}
	}

	public static void main(String[] args) throws Exception {
		String input = "input/seq_alignment";
		SeqAlignment sa = new SeqAlignment(new File(input + "/" + "BLOSUM62.txt"));
		
		args = new String[]{"Toy_FASTAs-in.txt"};
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt") ||
						file.getName().contains("62")){
					continue;
				}
				sa.loadFasta(file);
				sa.align();
			}
		} else{
			sa.loadFasta(new File(input + "/" + args[0]));
			sa.align();
		}
		
		sa.printResult();
	}
}
