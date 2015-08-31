package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashSet;


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
			else if(j1.getStartTime() > j2.getStartTime())
				return 1;
			else
				return 0;
		}
	});
	private List<Resource> resources;
	private Queue<Job> jobStack;
	
	public Scheduling(String path) throws FileNotFoundException{
		this(new File(path));
	}
	
	public Scheduling(File f) throws FileNotFoundException{
		readData(f);
	}

	private void readData(File file) throws FileNotFoundException{
		resources = new ArrayList<>();
		jobStack = new LinkedList<>();
		Scanner scan = new Scanner(file);
		int n = scan.nextInt();
		scan.nextLine();

		for (int i=0; i<n; i++) {
			int start = scan.nextInt();
			int end = scan.nextInt();
			Job job = new Job(start, end);
			job.setId(i);
			pq.add(job);
			jobStack.add(job);
		}
		scan.close();
	}

	public void solve(){
		while(!pq.isEmpty()){
			Job job = pq.poll();
			boolean assigned = false;
			for(int i=0; i<resources.size(); i++){
				if(!assigned && resources.get(i).getCurrentEndTime() <= job.getStartTime()){
					resources.get(i).addJob(job);
					job.setResId(i);
					assigned = true;
					break;
				}
			}
			
			//Not enough resources => we need to use another one
			if(!assigned){
				Resource res = new Resource();
				resources.add(res);
				res.addJob(job);
				job.setResId(res.getResourceNumber());
			}
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
	
	public void compareResult(File compareFile) throws FileNotFoundException{
		HashSet<String> resultSet = new HashSet<String>();
		
		for (Job j : jobStack) {
			resultSet.add(j.toString());
		}
		
		Scanner scan = new Scanner(compareFile);
		int resCount = scan.nextInt();
		if (resources.size() != resCount) {
			System.out.println("BAD: " + resCount + " <> " + resources.size());
			scan.close();
			return;
		}

		boolean isGood = true;
		while(scan.hasNextInt()) {
			int start = scan.nextInt();
			int end = scan.nextInt();
			int resId = scan.nextInt();
			Job resultJob = new Job(start, end);
			resultJob.setResId(resId);

			if (!resultSet.contains(resultJob.toString())) {
				System.out.println(resultJob.toString());
				isGood = false;
				break;
			}
		}
		scan.close();
		if (isGood) {
			System.out.println("OK: " + compareFile.getName());
		} else {
			System.out.println("BAD: " + compareFile.getName());
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
				//sch.printSolution();
				System.out.print(file.getName() + ": ");
				File outputFile = new File(input + "/" + file.getName().replaceAll("\\.in", ".out"));
				sch.compareResult(outputFile);
			} 
		} else{
			String filePath = args[0];
			Scheduling sch = new Scheduling(filePath);
			sch.solve();
			//sch.printSolution();
			System.out.println(filePath + ": ");
			sch.compareResult(new File(filePath.replaceAll("\\.in", ".out")));
		}
	}
}
