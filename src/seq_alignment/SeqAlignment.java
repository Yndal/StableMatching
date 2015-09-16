package seq_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;

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

		public String getSeq() {
			return sequence;
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
	int[][] A;
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
				System.out.println("run");
				int cost = align(fastaRecords.get(i).getSeq(), fastaRecords.get(j).getSeq());
				results.add(new Result(cost, fastaRecords.get(i), fastaRecords.get(j)));
				System.out.println("" + cost);
			}
		}
	}
	
	//This method might not be necessary :)
	private int align(CharSequence rec1, CharSequence rec2){
		if (0 == rec1.length()) {
			return -4 * rec2.length();
		}
		if (0 == rec2.length()) {
			return -4 * rec1.length();
		}
		int cost1 = -4 + align(rec1.subSequence(1, rec1.length()), rec2);
		int cost2 = -4 + align(rec1, rec2.subSequence(1, rec2.length()));
		int cost3 = alignmentData.get(String.valueOf(rec1.charAt(0))).get(String.valueOf(rec2.charAt(0))) + align(rec1.subSequence(1, rec1.length()), rec2.subSequence(1, rec2.length()));

		int maxCost = Math.max(Math.max(cost1, cost2), cost3);
		return maxCost;
	}
	
	private String lastLetter(String str){
		return str.substring(str.length()-1);
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
				if(file.getName().contains("out.txt") || file.getName().equals("BLOSUM62.txt")){
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
