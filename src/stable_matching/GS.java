package stable_matching;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class GS {
	private String inFile;
	Stack<Bro> bros = new Stack<>();
	Stack<Bro> engagedMen = new Stack<>();
	List<Chick> chicks = new ArrayList<>();
	Stack<Chick> engagedWomen = new Stack<>();
	//Person[] people;


	public GS(String path) throws Exception{
		inFile = path;
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
			Bro divorcedBro = bro.ProposalTo(chick);
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

	/*public void printResult(){
		while(!engagedMen.isEmpty())
			System.out.println(engagedMen.pop().getMarriageString());
	}*/

	public void compareResults() throws FileNotFoundException{
		List<String> in = new ArrayList<>();
		List<String> out = new ArrayList<>();
		while(!engagedMen.isEmpty())
			in.add(engagedMen.pop().getMarriageString());

		String outFile = getOutFilePath();
		Scanner scanner = new Scanner(new File(outFile));
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			if(!line.isEmpty()){
				out.add(line);			
			}
		}
		scanner.close();

		//System.out.println("Results:");
		int flaws = 0;
		for(int i=0; i<in.size(); i++){
			if(!out.contains(in.get(i))){
				flaws++;
			}
			//System.out.println(in.get(i));
		}

		System.out.println("Flaws: " + flaws);
	}

	public String getOutFilePath(){
		int index = inFile.lastIndexOf("in");
		String outFile = inFile.substring(0, index);
		outFile += "out";
		outFile += inFile.substring(index+2);
		return outFile;
	}

	public static void main(String[] args) throws Exception{
		if(args.length == 0){
			String input = "input/stable_matching";
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out"))
					continue;

				GS sm = new GS(input + "/" + file.getName());
				sm.solve();
				//sm.printResult();
				System.out.print(file.getName() + ": ");
				sm.compareResults();
			} 
		}else {
			String file = args[0];
			GS sm = new GS(file);
			sm.solve();
			//sm.printResult();
			System.out.print(file + ": ");
			sm.compareResults();
		}
	}

}

abstract class Person {
	int[] preferences;
	int[] invertedPreferences;
	int nextPrefered = 0;
	final int id;
	final String name;
	//boolean isEngaged = false;
	Person engagedTo;

	public Person(String name, int id, int[] preferences) {
		this.id = id;
		this.name = name;
		SetPreferences(preferences);
	}

	public Person(String name, int id) {
		this.id = id;
		this.name = name;
	}

	public boolean isEngaged(){
		return engagedTo != null; 
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void SetPreferences(int[] preferences) {
		this.preferences = preferences;
		invertedPreferences = new int[preferences.length];
		for(int i=0; i<preferences.length; i++)
			invertedPreferences[preferences[i]] = i;
	}
}

class Chick extends Person {
	public Chick(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}

	public Chick(String name, int id) {
		super(name, id);
	}

	public Bro acceptPropose(Bro bro){
		if(engagedTo == null){
			engagedTo = bro;

			return null;
		} else if(invertedPreferences[bro.getId()] 
				< invertedPreferences[engagedTo.getId()]){
			Bro divorcedFellow = (Bro) engagedTo;
			divorcedFellow.divorceFrom(this);
			engagedTo = bro;

			return divorcedFellow;
		} else {
			return null;
		}
	}
}

class Bro extends Person {
	public Bro(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}

	public Bro(String name, int id) {
		super(name, id);
	}

	public int getNextPrefered() {
		return preferences[nextPrefered++];
	}
	
	public Bro ProposalTo(Chick chick) {
		Bro divorcedBro = chick.acceptPropose(this);

		if(this == chick.engagedTo){
			engagedTo = chick;
		}

		return divorcedBro;
	}

	public void divorceFrom(Chick chick){
		engagedTo = null;
	}

	public String getMarriageString(){
		return this.name + " -- " + engagedTo.getName();
	}
}
