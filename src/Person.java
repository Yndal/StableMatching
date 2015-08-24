abstract public class Person {
	public Person(String name, int id, int[] preferences) {
		this.myId = id;
		this.myName = name;
		this.preferences = preferences
	}
}

public class Chick implements Person {
	public Chick(String name, int id, int[] preferences) {
		super(name, id, Preferences)
	}
}

public class Bro implements Person {
	private int[] preferences;
	private int nextPrefered = 0;
	private int id;
	private String name;

	public Bro(String name, int id, int[] preferences) {
		super(name, id, Preferences)
	}

	public int NextPreference() {
		return Preferences[nextPrefered++];
	}

	public void ProposeTo(int chick) {

	}
}
