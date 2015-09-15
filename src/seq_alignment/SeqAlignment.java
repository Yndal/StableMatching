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

		private FastaRecord(String name, String sequence){
			this.name = name;
			this.sequence = sequence;
		}

		public String getName(){
			return name;
		}

		public String getSequence(){
			return sequence;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private class Result{
		private final int cost;
		private final FastaRecord record1;
		private final FastaRecord record2;
		
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
	}

	private static final String letter_Gap = "*";
	private List<String> letters = new ArrayList<>();
	private HashMap<String, HashMap<String, Integer>> alignmentData = new HashMap<>();
	private List<FastaRecord> fastaRecords = new ArrayList<>();
	private List<Result> results = new ArrayList<>();
	
	public SeqAlignment(File blosumFile) throws FileNotFoundException{
		loadBlosum62(blosumFile);
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
		int c = results.size();
		c=1;
	}
	
	//This method might not be necessary :)
	private int align(FastaRecord rec1, FastaRecord rec2){
		return align(rec1.sequence, rec2.sequence);
	}
	
	private int align(String seq1, String seq2){
		//Initialize arrays
		int m = seq1.length();
		int n = seq2.length();
		int[][] A = new int[m][n];
		for (int i=0; i<m; i++){
			A[i][0] = (i+1) * getCost(seq1.substring(i, i+1), letter_Gap);
		}
		for (int j=0; j<n; j++){
			A[0][j] = (j+1) * getCost(letter_Gap, seq2.substring(j, j+1));
		}		
		//Alignment cost calculation with recurrence
		//TODO: Implement
		
		return A[m-1][n-1];
	}
	
	public boolean printResult(){
		//TODO: Implement
		return false;
	}

	public static void main(String[] args) throws Exception {
		String input = "input/seq_alignment";
		SeqAlignment sa = new SeqAlignment(new File(input + "/" + "BLOSUM62.txt"));
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt")){
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
