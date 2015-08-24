abstract class Person {
	int[] preferences;
	int[] invertedPreferences;
	int nextPrefered = 0;
	int id;
	String name;

	public Person(String name, int id, int[] preferences) {
		this.id = id;
		this.name = name;
		this.preferences = preferences;
	}
}

class Chick extends Person {
	public Chick(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}
	
	public boolean acceptPropose(Bro bro){
		
		
		return true;
	}
}

class Bro extends Person {
	public Bro(String name, int id, int[] preferences) {
		super(name, id, preferences);
	}

	public int NextPreference() {
		return preferences[nextPrefered++];
	}

	public void ProposeTo(Chick chick) {
		chick.acceptPropose(this);
	}
}
