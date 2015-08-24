abstract class Person {
	final int[] preferences;
	final int[] invertedPreferences;
	int nextPrefered = 0;
	final int id;
	final String name;
	//boolean isEngaged = false;
	Person engagedTo;
	
	public Person(String name, int id, int[] preferences) {
		this.id = id;
		this.name = name;
		this.preferences = preferences;
		invertedPreferences = new int[preferences.length];
		for(int i=0; i<preferences.length; i++)
			invertedPreferences[preferences[i]] = i;
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
}

class Chick extends Person {
	Bro engagedTo;
	
	public Chick(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}
	
	public boolean acceptPropose(Bro bro){
		if(nextPrefered <= invertedPreferences[bro.getId()]){
			nextPrefered = invertedPreferences[bro.getId()]+1;
			
			divorce(engagedTo);
			engagedTo = bro;
			
			return true;
		}
		
		return false;
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
	
	public int NextPreference() {
		return preferences[nextPrefered++];
	}
	
	public int getNextPrefered(){
		return nextPrefered;
	}

	public void ProposeTo(Chick chick) {
		if(chick.acceptPropose(this)){
			engagedTo = chick;
		}
	}
	
	public void divorceFrom(Chick chick){
		engagedTo = null;
	}
	
	public String getMarriageString(){
		return this.name + " -- " + engagedTo.getName();
	}
}
