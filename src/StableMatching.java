import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class StableMatching {
	
	Stack<Bro> bros = new Stack<>();
	Stack<Bro> engagedMen = new Stack<>();
	List<Chick> chicks = new ArrayList<>();
	Stack<Chick> engagedWomen = new Stack<>();
	Person[] people;
	
	
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
		
		Bro[] men = new Bro[n];
		Chick[] women = new Chick[n];
		//first field emtpy, indexed by ids from input
		people = new Person[n*2];
		
		boolean man = true;
		int menidx=0;
		int womenidx=0;
		while(!(str = scanner.nextLine()).isEmpty()){
			int index = str.indexOf(" ");
			int number = Integer.valueOf(str.substring(0, index)) -1;
			String name = str.substring(index + 1);

			if (man) {
				Bro bro = new Bro(name, number);
				men[menidx++] = bro;
				people[number] = bro;
			} else {
				Chick chick = new Chick(name, number);
				women[womenidx++] = chick;
				people[number] = chick;
			}
			man = !man;
		}
		
		int p = 0;
		while(scanner.hasNextLine() && !(str = scanner.nextLine()).isEmpty()){
			int person = str.indexOf(":");
			//int personIndex = Integer.valueOf(str.substring(0, person));
			String priorities = str.substring(str.indexOf(' ')+1);
			int[] prefs = new int[n];
			for(int i=0; i<n; i++){
				int index1 = priorities.indexOf(' ');
				
				int prio = Integer.valueOf(priorities.substring(0, index1 == -1 ? priorities.length() : index1));
				--prio; //0 indexed
				priorities = priorities.substring(priorities.indexOf(' ')+1);
				prefs[i] = prio;
			}
			people[p++].SetPreferences(prefs);
		}
		
		scanner.close();
		
		for(int i=0; i<men.length; i++){
			bros.add(men[i]);
			chicks.add(women[i]);
		}
	}
	
	public void solve(){
		while(!bros.isEmpty()){
			Bro bro = bros.pop();
			Chick chick = (Chick) people[bro.getNextPrefered()];
			Bro divorcedBro = bro.ProposeTo(chick);
			if(chick.engagedTo == bro){
				engagedMen.push(bro);
			} else
				bros.push(bro);
			
			if(divorcedBro != null){
				bros.push(divorcedBro);
				engagedMen.remove(divorcedBro);
			} 
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
