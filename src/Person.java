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
		} else if(invertedPreferences.get(bro.getId()) 
				< invertedPreferences.get(
						this.engagedTo.getId())){
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
	
	public Bro ProposeTo(Chick chick) {
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
