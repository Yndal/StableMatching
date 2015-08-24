import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StableMatching {
	
	public StableMatching(String path) throws Exception{
		File file = new File(path);
		readData(file);
	}
	
	public void readData(File file) throws FileNotFoundException{
		Scanner scanner = new Scanner(file);
	}
	
	public void main(String[] args) throws Exception{
		String filepath = "input/sm-bbt-in.txt";
		StableMatching sm = new StableMatching(filepath);
	}
}
