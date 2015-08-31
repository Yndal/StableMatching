package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;


class Job {
	private final int id;
	private final int startTime;
	private final int jobTime;

	public Job(int start, int time, int id){
		this.id = id;
		this.startTime = start;
		this.jobTime = time;
	}

	public int getId(){
		return id;
	}
	public int getStartTime() {
		return startTime;
	}

	public int getJobTime() {
		return jobTime;
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
		currentEndTime += job.getJobTime();
	}

	public int getCurrentEndTime(){
		return currentEndTime;
	}
	
	public int getResourceNumber(){
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
			else if(j1.getStartTime() < j2.getStartTime())
				return 1;
			else
				return 0;
		}
	});
	private List<Resource> resources;
	private Stack<Job> jobStack;
	
	public Scheduling(String path) throws FileNotFoundException{
		this(new File(path));
	}
	
	public Scheduling(File f) throws FileNotFoundException{
		readData(f);
	}

	private void readData(File file) throws FileNotFoundException{
		resources = new ArrayList<>();
		jobStack = new Stack<>();
		Scanner scan = new Scanner(file);
		int n = scan.nextInt();
		scan.nextLine();

		for (int i=0; i<n; i++) {
			int start = scan.nextInt();
			int end = scan.nextInt();
			Job job = new Job(start, end, i);
			pq.add(job);
			jobStack.add(job);
		}
	}

	public void solve(){
		while(!pq.isEmpty()){
			Job job = pq.poll();
			boolean assigned = false;
			for(int i=0; i<resources.size(); i++){
				if(resources.get(i).getCurrentEndTime() <= job.getStartTime()){
					resources.get(i).addJob(job);
					assigned = true;
				}
			}
			
			//Not enough resources => we need to use another one
			if(!assigned){
				Resource res = new Resource();
				resources.add(res);
				res.addJob(job);
			}
		}
	}
	
	
	//This method is not optimized as it is not an actual part of the running time ;-)
	public void printSolution(){
		System.out.println(resources.size() + "\n");
		while(!jobStack.isEmpty()){
			Job job = jobStack.pop();
			System.out.println(job.getStartTime() + " " + job.getJobTime() + " " + job.getId());
		}
	}
	
	public static void main(String[] args) throws Exception{
		if(args.length == 0){
			String input = "input/scheduling";
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out"))
					continue;

				Scheduling sch = new Scheduling(input + "/" + file.getName());
				sch.solve();
				sch.printSolution();
				System.out.print(file.getName() + ": ");
				//sm.compareResults();
			} 
		}else {
			String file = args[0];
			Scheduling sch = new Scheduling(file);
			sch.solve();
			sch.printSolution();
			System.out.print(file + ": ");
			//sm.compareResults();
		}
	}
}
