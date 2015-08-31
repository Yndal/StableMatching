package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashSet;
import java.util.Iterator;


class Job {
	private int resId;
	private final int startTime;
	private final int endTime;
	private int id;

	public Job(int start, int time){
		this.startTime = start;
		this.endTime = time;
	}

	public void setResId(int id){
		this.resId = id;
	}
	public int getResId(){
		return resId;
	}
	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String toString(){
		return this.getStartTime() + " " + this.getEndTime() + " " + this.getResId();
	}
}

class Resource{
	private final List<Job> jobs = new ArrayList<>();
	private int currentEndTime = 0;
	private final int thisResourceId;
	private static int resourceCounter = 0;

	public Resource(){
		thisResourceId = resourceCounter++;
	}

	public void addJob(Job job){
		jobs.add(job);
		currentEndTime = job.getEndTime();
	}

	public int getCurrentEndTime(){
		return currentEndTime;
	}

	public int getResourceId(){
		return thisResourceId;
	}
}

public class Scheduling {
	private final PriorityQueue<Job> pq = new PriorityQueue<>(new Comparator<Job>() {
		@Override
		public int compare(Job j1, Job j2) {
			//Compare by starting time
			if(j1.getStartTime() < j2.getStartTime())
				return -1;
			else if(j1.getStartTime() > j2.getStartTime())
				return 1;
			else
				return 0;
		}
	});
	private PriorityQueue<Resource> resources = new PriorityQueue<>(new Comparator<Resource>() {

		@Override
		public int compare(Resource o1, Resource o2) {
			if(o1.getCurrentEndTime() < o2.getCurrentEndTime())
				return -1;
			else if(o1.getCurrentEndTime() > o2.getCurrentEndTime())
				return 1;
			else
				return 0;			
		}

	});
	private Queue<Job> jobStack;

	public Scheduling(String path) throws FileNotFoundException{
		this(new File(path));
	}

	public Scheduling(File f) throws FileNotFoundException{
		readData(f);
	}

	private void readData(File file) throws FileNotFoundException{
		jobStack = new LinkedList<>();
		Scanner scan = new Scanner(file);
		int n = scan.nextInt();
		scan.nextLine();

		for (int i=0; i<n; i++) {
			int start = scan.nextInt();
			int end = scan.nextInt();

			Job job = new Job(start, end);
			//	job.setId(i);
			pq.add(job);
			jobStack.add(job);
		}
		scan.close();
	}

	public void solve(){
		while(!pq.isEmpty()){
			Job job = pq.poll();
			Resource resource = resources.poll();

			if(resource == null){
				resource = new Resource();
				resource.addJob(job);
				job.setResId(resource.getResourceId());
			} else if (resource.getCurrentEndTime() <= job.getStartTime()){
				resource.addJob(job);
				job.setResId(resource.getResourceId());					
			} else {
				resources.add(resource);
				resource = new Resource();
				resource.addJob(job);
				job.setResId(resource.getResourceId());

			}

			resources.add(resource);
		}
	}


	//This method is not optimized as it is not an actual part of the running time ;-)
	public void printSolution(){
		System.out.println(resources.size() + "\n");
		while(!jobStack.isEmpty()){
			Job job = jobStack.poll();
			System.out.println(job);
		}
	}

	public boolean compareResult(File compareFile) throws FileNotFoundException{
		Set<String> resultSet = new HashSet<String>();

		for (Job j : jobStack)
			resultSet.add(j.toString().toLowerCase().trim());

		Scanner scan = new Scanner(compareFile);
		int resCount = scan.nextInt();
		scan.close();
		if (resources.size() != resCount) {
			System.out.println("Bad resource assigning: " + resCount + " <> " + resources.size());
			scan.close();
			return false;
		}

		return true;

		/*boolean isGood = true;
		int counter = 0;
		while(scan.hasNextLine()) {
			String line = scan.nextLine().toLowerCase().trim();
			if(line.isEmpty())
				continue;
			if(!resultSet.contains(line)){
				//System.out.println("Not found in result: " + line);

				Iterator<String> it = resultSet.iterator();
				while(it.hasNext()){
					String s = it.next();
					String ss = line.substring(0, line.lastIndexOf(" "));
					if(s.startsWith(ss))
						System.out.println("our: " + s + "\nread: " + line + "\n");

				}

				isGood = false;
				counter++;
				//break;
			};

		}

		if (isGood) {
			System.out.println("OK: " + compareFile.getName());
		} else {
			System.out.println("BAD: " + compareFile.getName() + "( flaws: " + counter + ")");
		}*/


	}

	public static void main(String[] args) throws Exception{
		//		String input = "input/scheduling/";
		//		String filePath = input + "ip-rand-1k.in";
		//		Scheduling sch = new Scheduling(filePath);
		//		sch.solve();
		//		//sch.printSolution();
		//		System.out.println(filePath + ": ");
		//		sch.compareResult(new File(filePath.substring(0,filePath.lastIndexOf(".")) + ".out"));
		if(args.length == 0){
			String input = "input/scheduling";
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out"))
					continue;

				Scheduling sch = new Scheduling(input + "/" + file.getName());
				sch.solve();
				//sch.printSolution();
				System.out.print(file.getName() + " is ok? ");
				File outputFile = new File(input + "/" + file.getName().replaceAll("\\.in", ".out"));
				System.out.println(sch.compareResult(outputFile));
			} 
		} else{
			String filePath = args[0];
			Scheduling sch = new Scheduling(filePath);
			sch.solve();
			//sch.printSolution();
			System.out.print(filePath + " is ok? ");
			System.out.println(sch.compareResult(new File(filePath.replaceAll("\\.in", ".out"))));
		}
	}
}
