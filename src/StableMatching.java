import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class StableMatching {
	
	Stack<Bro> bros = new Stack<>();
	Stack<Bro> engagedMen = new Stack<>();
	List<Chick> women = new ArrayList<>();
	Stack<Chick> engagedWomen = new Stack<>();
		
	
	public StableMatching(String path) throws Exception{
		File file = new File(path);
		readData(file);
	}
	
	public void readData(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
		
		//Skip the first lines of comments
		String str;
		while((str = scanner.nextLine()).startsWith("#"));
		
		str = str.substring(str.indexOf('=')+1);
		int n = Integer.valueOf(str);
		System.out.println("n: " + n);
		
		boolean man = true;
		int menidx=0;
		int womenidx=0;
		while(!(str = scanner.nextLine()).isEmpty()){
			int index = str.indexOf(" ");
			int number = Integer.valueOf(str.substring(0, index));
			String name = str.substring(index + 1);

			if (man) {
				;//men[menidx++] = new Bro(name, number);
			} else {
				;//women[womenidx++] = new Chick(name, number);
			}
			man = !man;
		}
		
		while(scanner.hasNextLine() && !(str = scanner.nextLine()).isEmpty()){
			int person = str.indexOf(":");
			int personIndex = Integer.valueOf(str.substring(0, person));
			String priorities = str.substring(str.indexOf(' ')+1);
				for(int i=0; i<n; i++){
					int index1 = priorities.indexOf(' ');
					
					int prio = Integer.valueOf(priorities.substring(0, index1 == -1 ? priorities.length() : index1));
					priorities = priorities.substring(priorities.indexOf(' ')+1);
					System.out.print(prio + " ");
				}
				System.out.println();			
		}
		
		scanner.close();
	}
	
	public void solve(){
		while(!bros.isEmpty()){
			Bro bro = bros.pop();
			Chick chick = women.get(bro.getNextPrefered());
			Bro divorcedBro = bro.ProposeTo(chick);
			if(divorcedBro != null)
				bros.push(divorcedBro);
		}
	}
	
	public void printResult(){
		while(!engagedMen.isEmpty())
			System.out.println(engagedMen.pop().getMarriageString());
	}
	
	public static void main(String[] args) throws Exception{
		String filepath = "input/sm-bbt-in.txt";
		StableMatching sm = new StableMatching(filepath);
		sm.solve();
		sm.printResult();
		
	}

}
