import java.util.Map;
import java.util.HashMap;

abstract class Person {
	int[] preferences;
	Map<Integer,Integer> invertedPreferences;
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
		invertedPreferences = new HashMap<Integer,Integer>();
		for(int i=0; i<preferences.length; i++)
			invertedPreferences.put(preferences[i], i);
	}
}

class Chick extends Person {
	Bro engagedTo;
	
	public Chick(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}

	public Chick(String name, int id) {
		super(name, id);
	}
	
	public Bro acceptPropose(Bro bro){
		if(nextPrefered <= invertedPreferences.get(bro.getId())){
			nextPrefered = invertedPreferences.get(bro.getId())+1; //TODO Should this be minus
			
			Bro xBro = engagedTo;
			if(engagedTo != null){
				divorce(engagedTo);
			}
			engagedTo = bro;
			
			return xBro;
		}
		
		return null;
	}
	
	private void divorce(Bro bro){
		bro.divorceFrom(this);
		engagedTo = null;
	}
}

class Bro extends Person {
	public Bro(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}

	public Bro(String name, int id) {
		super(name, id);
	}

	public int NextPreference() {
		return preferences[nextPrefered++];
	}
	
	public int getNextPrefered(){
		return nextPrefered;
	}

	public Bro ProposeTo(Chick chick) {
		Bro divorcedBro = chick.acceptPropose(this);
		if(divorcedBro != null){
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
