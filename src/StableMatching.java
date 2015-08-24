import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StableMatching {
	
	int[] men;
	int[] women;
	
	
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
		
		men = new int[n];
		women = new int[n];
		
		while(!(str = scanner.nextLine()).isEmpty()){
			int index = str.indexOf(" ");
			int number = Integer.valueOf(str.substring(0, index));
			String name = str.substring(index + 1);
			System.out.println("number: " + number + ", name: " + name);
		}
		
		System.out.println("---------");
		
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
	
	
	
	
	public static void main(String[] args) throws Exception{
		String filepath = "input/sm-bbt-in.txt";
		StableMatching sm = new StableMatching(filepath);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}