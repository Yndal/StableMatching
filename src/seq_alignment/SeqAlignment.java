package seq_alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SeqAlignment {
	private class FastaRecord{
		private final int id;
		private final String name;
		private final String sequence;

		private FastaRecord(int id, String name, String sequence){
			this.id = id;
			this.name = name;
			this.sequence = sequence;
		}

		public int getId(){
			return id;
		}

		public String getName(){
			return name;
		}

		public String getSequence(){
			return sequence;
		}

		@Override
		public String toString() {
			return id + ": " + name;
		}
	}

	private List<FastaRecord> fastaRecords = new ArrayList<>();
	
	public SeqAlignment(){
	}
	
	public SeqAlignment(File fastaFile) throws FileNotFoundException{
		loadFasta(fastaFile);
	}
	
	private void loadFasta(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		String s;
		while(scanner.hasNextLine()){
			s = scanner.nextLine();
			if (s.contains(">")){
				fastaRecords.add(new FastaRecord(-1, "name", "seq"));
			}
			//TODO: Implement parser
		}
		scanner.close();
	}

	public double align(){
		//TODO: Implement fancy stuff
		return -1;
	}

	public boolean printResult(){
		//TODO: Implement
		return false;
	}

	public static void main(String[] args) throws Exception {
		SeqAlignment sa = new SeqAlignment();
		String input = "input/seq_alignment";
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out")){
					continue;
				}
				sa.loadFasta(file);

			} 
		} else{
			sa.loadFasta(new File(input + "/" + args[0]));
		}
		sa.align();
		sa.printResult();
	}
}
