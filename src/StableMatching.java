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
	//Person[] people;
	
	
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
		//people = new Person[n*2];
		
		while(!(str = scanner.nextLine()).isEmpty()){
			int index = str.indexOf(" ");
			int number = Integer.valueOf(str.substring(0, index)) -1;
			String name = str.substring(index + 1);

			if (number%2 == 0) {
				Bro bro = new Bro(name, number/2);
				men[number/2] = bro;
			} else {
				Chick chick = new Chick(name, number/2);
				women[number/2] = chick;
			}
		}
		
		while(scanner.hasNextLine() && !(str = scanner.nextLine()).isEmpty()){
			int person = str.indexOf(":");
			int personIndex = Integer.valueOf(str.substring(0, person)) - 1;
			String priorities = str.substring(str.indexOf(' ')+1);
			int[] prefs = new int[n];
			for(int i=0; i<n; i++){
				int index1 = priorities.indexOf(' ');
				
				int prio = Integer.valueOf(priorities.substring(0, index1 == -1 ? priorities.length() : index1));
				--prio; //0 indexed
				priorities = priorities.substring(priorities.indexOf(' ')+1);
				prefs[i] = prio/2;
			}
			if(personIndex % 2 == 0)
				men[personIndex/2].SetPreferences(prefs);
			else 
				women[personIndex/2].SetPreferences(prefs);
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
			Chick chick = chicks.get(bro.getNextPrefered());
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
		String filepath = args.length == 0 ? "input/sm-bbt-in.txt" : args[0];
		StableMatching sm = new StableMatching(filepath);
		sm.solve();
		sm.printResult();
		
	}

}
