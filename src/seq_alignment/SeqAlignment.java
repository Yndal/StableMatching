package seq_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SeqAlignment {
	private static class Pair<T,T2>{
		public final T first;
		public final T2 second;
		public Pair(T first, T2 second) {
			this.first = first;
			this.second = second;
		}
	}

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

		public String getSeq() {
			return sequence;
		}
	}
	
	private class Result{
		private final int cost;
		private final FastaRecord record1;
		private final FastaRecord record2;
		private final String alignment;

		public Result(int cost, FastaRecord record1, FastaRecord record2, String alignment){
			this.cost = cost;
			this.record1 = record1;
			this.record2 = record2;
			this.alignment = alignment;
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
		public String toString() {
			return record1.getName() + "--" +
				record2.getName() +": " +
				cost +"\n" +
				alignment;
		}
	}

	private static final String letter_Gap = "*";
	private static boolean DEBUG;
	private List<String> letters = new ArrayList<>();
	private HashMap<String, HashMap<String, Integer>> alignmentData = new HashMap<>();
	private List<FastaRecord> fastaRecords = new ArrayList<>();
	int[][] A;
	
	public SeqAlignment(File blosumFile) throws FileNotFoundException{
		loadBlosum62(blosumFile);
	}
	
	private int getCost(String letter1, String letter2){
		return alignmentData.get(letter1).get(letter2);
	}

	private int getCost(char letter1, char letter2){
		return alignmentData.get(letter1 + "").get(letter2 + "");
	}

	private int getCost(String letter1, char letter2){
		return alignmentData.get(letter1).get(letter2 + "");
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

	public List<Result> align(){
		List<Result> results = new ArrayList<>();
		for (int i=0; i<fastaRecords.size()-1; i++){
			for (int j=i+1; j<fastaRecords.size(); j++){
				String seq1 = fastaRecords.get(i).getSeq();
				String seq2 = fastaRecords.get(j).getSeq();

				int[][] costMatrix = align(seq1, seq2);
				int cost = costMatrix[costMatrix.length -1][costMatrix[0].length -1];

				Pair<String, String> matching = traceMatching(seq1, seq2, costMatrix);
				String alignmentString = matching.first + "\n" + matching.second;
				results.add(new Result(cost, fastaRecords.get(i), fastaRecords.get(j), alignmentString));
				if(DEBUG) {
					System.out.println("cost: " + cost);
					System.out.println(matching.first);
					System.out.println(matching.second);
					System.out.println();
				}
			}
		}
		return results;
	}

	private int[][] align(String seq1, String seq2) {
		int m = seq1.length() +1;
		int n = seq2.length() +1;

		int[][] results = new int[m][n];

		for(int i=1; i<m; i++) {
			results[i][0] = i*getCost(letter_Gap, seq1.charAt(i-1) + "");
		}

		for(int i=1; i<n; i++) {
			results[0][i] = i*getCost(letter_Gap, seq2.charAt(i-1) + "");
		}

		for (int i=1; i < m; i++) {
			for (int j=1; j < n; j++) {
				int cost1 = getCost(letter_Gap, seq2.charAt(j-1) + "") + results[i-1][j];
				int cost2 = getCost(letter_Gap, seq1.charAt(i-1) + "") + results[i][j-1];
				int cost3 = getCost(seq2.charAt(j-1) + "", seq1.charAt(i-1) + "") + results[i-1][j-1];
				results[i][j] = Math.max(Math.max(cost1, cost2), cost3);
			}
		}

		if(DEBUG) {
			System.out.print("         ");
			for (int i = 0; i<n-1; i++) {
				System.out.print(seq2.charAt(i) + "    ");
			}
			for (int i = 0; i<m; i++) {
				System.out.println();
				if(i!=0) {
					System.out.print(seq1.charAt(i-1) + " ");
				} else {
					System.out.print("  ");
				}
				for (int j = 0; j<n; j++) {
					System.out.printf("%3d, ", results[i][j]);
				}
			}
			System.out.println();
		}
		return results;
	}

	private Pair<String,String> traceMatching(String seq1, String seq2, int[][] costMat) {
		String m1 = "";
		String m2 = "";
		int m = costMat.length;
		int n = costMat[0].length;

		int i=m-1, j=n-1;
		while(j>0 || i>0) {
			int cost1 = !(i>0) ? Integer.MIN_VALUE : costMat[i-1][j] + getCost(letter_Gap, seq1.charAt(i-1));
			int cost2 = !(j>0) ? Integer.MIN_VALUE : costMat[i][j-1] + getCost(letter_Gap, seq2.charAt(j-1));
			int cost3 = !(j>0 && i>0) ? Integer.MIN_VALUE : costMat[i-1][j-1] + getCost(seq1.charAt(i-1) + "", seq2.charAt(j-1)+"");

			int maxCost = Math.max(cost1, Math.max(cost2, cost3));

			if (cost1 == maxCost) {
				m2 = "-" + m2;
				m1 = seq1.charAt(i-1) + m1;
				i--;
			} else if (cost2 == maxCost) {
				m1 = "-" + m1;
				m2 = seq2.charAt(j-1) + m2;
				j--;
			} else if (cost3 == maxCost) {
				m1 = seq1.charAt(i-1)+ m1;
				m2 = seq2.charAt(j-1)+ m2;
				j--;
				i--;
			}
		}
		return new Pair<String,String>(m1,m2);
	}

	private String lastLetter(String str){
		return str.substring(str.length()-1);
	}
	
	public static void printResult(List<Result> res){
		for (Result r : res) {
			System.out.println(r);
		}
	}

	public static void main(String[] args) throws Exception {
		String input = "input/seq_alignment";
		SeqAlignment sa = new SeqAlignment(new File(input + "/" + "BLOSUM62.txt"));
		Pair<String,String> p = new Pair<>("lol", "lel");
		List<Pair<String, List<Result>>> results = new ArrayList<>();
		DEBUG=false;
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt") || file.getName().contains("62")){
					continue;
				}
				sa.loadFasta(file);
				results.add(new Pair<>(file.getName(), sa.align()));
			}
		} else{
			sa.loadFasta(new File(input + "/" + args[0]));
			results.add(new Pair<String,List<Result>>(args[0], sa.align()));
		}

		for(Pair<String, List<Result>> rl : results) {
			System.out.println("File: " + rl.first);
			sa.printResult(rl.second);
			System.out.println();
		}
	}
}
