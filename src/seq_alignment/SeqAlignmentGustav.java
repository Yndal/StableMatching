package seq_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SeqAlignmentGustav {
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

		public String getSeguence() {
			return sequence;
		}
	}
	
	private class Result{
		private final int cost;
		private final String name1;
		private final String name2;
		private final String sequence1;
		private final String sequence2;
		
		public Result(int cost, String name1, String sequence1, String name2, String sequence2){
			this.cost = cost;
			this.name1 = name1;
			this.sequence1 = sequence1;
			this.name2 = name2;
			this.sequence2 = sequence2;			
		}
		
		public Result(int cost, FastaRecord record1, FastaRecord record2){
			this.cost = cost;
			this.name1 = record1.name;
			this.sequence1 = record1.sequence;
			this.name2 = record2.name;
			this.sequence2 = record2.sequence;
		}
		
		public boolean equals(Result otherResult, boolean checkSequence){
			if (otherResult.cost != this.cost)
				return false;
			if (!otherResult.name1.equals(this.name1) && !otherResult.name1.equals(this.name2))
				return false;
			if (!otherResult.name2.equals(this.name2) && !otherResult.name2.equals(this.name1))
				return false;
			if (!checkSequence)
				return true;
			boolean swappedNames = otherResult.name1.equals(this.name2);
			if (!swappedNames){
				if (!otherResult.sequence1.equals(this.sequence1) || !otherResult.sequence2.equals(this.sequence2))
					return false;
			}
			else{
				if (!otherResult.sequence1.equals(this.sequence2) || !otherResult.sequence2.equals(this.sequence1))
					return false;				
			}
			return true;
		}
		
		public int getCost(){
			return cost;
		}
		
		public String getName1(){
			return name1;
		}
		
		public String getName2(){
			return name2;
		}
		
		public String getSequence1(){
			return sequence1;
		}
		
		public String getSequence2(){
			return sequence2;
		}
	}
	
	private HashMap<String, HashMap<String, Integer>> alignmentData = new HashMap<>();
	private String fastaFilename = null;
	private List<FastaRecord> fastaRecords = new ArrayList<>();
	private static final String letter_Gap = "*";
	private List<String> letters = new ArrayList<>();
	private List<Result> results = new ArrayList<>();
	
	public SeqAlignmentGustav(File blosumFile) throws FileNotFoundException{
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
	
	private void loadFasta(File file, boolean useShortNames) throws FileNotFoundException{
		fastaFilename = file.getName();
		Scanner scanner = new Scanner(file);
		String s, name=null, seq=null;
		while(scanner.hasNextLine()){
			s = scanner.nextLine();
			if (s.contains(">")){
				if (name != null && !name.isEmpty()){
					fastaRecords.add(new FastaRecord(name, seq));
				}
				name = s.substring(1).trim();
				if (useShortNames){
					int index = name.indexOf(" ");
					if (index < 1)
						index = name.length();
					name = name.substring(0, index);
				}
				seq = "";
			}
			else if(!s.trim().isEmpty()){
				seq += s.trim();
			}
		}
		scanner.close();
		if (name != null && !name.isEmpty()){
			fastaRecords.add(new FastaRecord(name, seq));
		}
	}

	public void align(){
		for (int i=0; i<fastaRecords.size()-1; i++){
			for (int j=i+1; j<fastaRecords.size(); j++){
				results.add(align(fastaRecords.get(i), fastaRecords.get(j)));
			}
		}
	}
	
	private Result align(FastaRecord rec1, FastaRecord rec2){
		//The matrix
		String seq1 = rec1.sequence;
		String seq2 = rec2.sequence;
		int m = seq1.length();
		int n = seq2.length();		

		int[][] A = new int[m+1][n+1];
		A[0][0] = 0;
		for (int i=0; i<m; i++){
			A[i+1][0] = (i+1) * getCost(seq1.substring(i, i+1), letter_Gap);
		}
		for (int j=0; j<n; j++){
			A[0][j+1] = (j+1) * getCost(letter_Gap, seq2.substring(j, j+1));
		}
		
		for (int j=1; j<=n; j++){
			for (int i=1; i<=m; i++){
				String letter1 = seq1.substring(seq1.length()-i, seq1.length()-i+1);
				//System.out.println("i=" + i + ", " + "seq1=" + seq1 + ", " + "l1=" + letter1);
				String letter2 = seq2.substring(seq2.length()-j, seq2.length()-j+1);
				//System.out.println("j=" + j + ", " + "seq2=" + seq2 + ", " + "ll2=" + letter2);
				int alpha1 = getCost(letter1, letter2);
				int delta2 = getCost(letter_Gap, letter1);
				int delta3 = getCost(letter_Gap, letter2);
				int cost1 = alpha1 + A[i-1][j-1];
				int cost2 = delta2 + A[i-1][j];
				int cost3 = delta3 + A[i][j-1];				
				A[i][j] = Math.max(cost1, Math.max(cost2, cost3));
				//printMatrix(A);
			}
		}
		
		//The sequences - procedure borrowed/stolen from REPO :)
		String alignedSequence1 = "";
		String alignedSequence2 = "";
		
		int bestI = m, bestJ = n;
		while (bestI>0 && bestJ>0){
			//Case 1: Char from seq1
			int cost1 = -4 + A[bestI-1][bestJ];
			
			//Case 2: Char from seq2
			int cost2 = -4 + A[bestI][bestJ-1];
			
			//Case 3: Char from seq1 and seq2
			int cost3 = A[bestI][bestJ] + A[bestI-1][bestJ-1];
			
			int max = Math.max(cost1, Math.max(cost2, cost3));
			
			if(cost1 == max){
				alignedSequence1 = seq1.charAt(bestI-1) + alignedSequence1;
				alignedSequence2 = "-" + alignedSequence2;	
				bestI--;
			} else if(cost2 == max){
				alignedSequence1 = "-" + alignedSequence1;
				alignedSequence2 = seq2.charAt(bestJ-1) + alignedSequence2;
				bestJ--;
			} else if (cost3 == max){
				alignedSequence1 = seq1.charAt(bestI-1) + alignedSequence1;
				alignedSequence2 = seq2.charAt(bestJ-1) + alignedSequence2;
				bestI--;
				bestJ--;
			}
		}

		//The result
		Result result = new Result(A[m][n], rec1.name, alignedSequence1, rec2.name, alignedSequence2);
		return result;
	}
		
	public void clearFastaAndResults(){
		fastaFilename = null;
		fastaRecords.clear();
		results.clear();
	}
	
	private Result getResult(List<Result> results, String filterName1, String filterName2){
		Result result = null;
		for (Result r : results){
			if ((r.name1.equals(filterName1) && r.name2.equals(filterName2)) || (r.name1.equals(filterName2) && r.name2.equals(filterName1)))
				result = r;
		}
		return result;
	}
	
	private void printMatrix(int[][] matrix){
		System.out.println("Matrix:");
		for (int i=0; i<matrix.length; i++) {
			String line = "";
			for (int j=0; j<matrix[0].length; j++){
				line += " " + matrix[i][j];
			}
			System.out.println(line);
		}
		System.out.println("");
	}
	
	public boolean printResult(boolean useShortNames) throws FileNotFoundException {
		//Parse their results
		List<Result> theirResults = new ArrayList<>();
		String resultFilePath = "input/seq_alignment/" + fastaFilename.replace("in",  "out");
		Scanner scanner = new Scanner(new File(resultFilePath));
		while(scanner.hasNextLine()){
			String s = scanner.nextLine();
			if (s.isEmpty())
				continue;
			String name1 = s.substring(0, s.indexOf("--"));
			String name2 = s.substring(name1.length()+2, s.indexOf(":"));
			int cost = Integer.parseInt(s.substring(s.indexOf(":")+1).trim());
			String sequence1 = scanner.nextLine();
			String sequence2 = scanner.nextLine();
			Result result = new Result(cost, name1, sequence1, name2, sequence2);
			theirResults.add(result);
		}
		scanner.close();
		//Compare with our results
		System.out.println("Results (" + results.size() + "/" + theirResults.size() +")" + ":");
		for (Result ourResult : results){
			String ourName1 = ourResult.name1;
			String ourName2 = ourResult.name2;
			if (useShortNames){
				int index1 = ourName1.indexOf(" ");
				if (index1<1)
					index1 = ourName1.length();
				ourName1 = ourName1.substring(0, index1);
				int index2 = ourName2.indexOf(" ");
				if (index2<1)
					index2 = ourName2.length();
				ourName2 = ourName2.substring(0, index2);
			}
			String status = "FAIL";
			Result theirResult = getResult(theirResults, ourName1, ourName2);
			if (theirResult.equals(ourResult, false))
				status = "OK";
			System.out.println(ourName1 + "--" + ourName2 + ": " + ourResult.cost + "(" + status + ")");
		}
		System.out.println("");
		return false;
	}

	public static void main(String[] args) throws Exception {
		String input = "input/seq_alignment";
		SeqAlignmentGustav sa = new SeqAlignmentGustav(new File(input + "/" + "BLOSUM62.txt"));
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out.txt") || file.getName().contains("BLOSUM")){
					continue;
				}
				sa.loadFasta(file, true);
				sa.align();
				System.out.println(file.getName() + ": ");
				sa.printResult(true);
				sa.clearFastaAndResults();
			} 
		} else{
			sa.loadFasta(new File(input + "/" + args[0]), true);
			sa.align();
			sa.printResult(true);
		}
	}
}



/*
*/