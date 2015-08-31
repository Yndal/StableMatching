package scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

class Job {
	private final int startTime;
	private final int jobTime;
	private final int id;

	public Job(int id, int start, int time){
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
	private List<Job> unassignedJobs;
	private Stack<Job> jobStack;
	
	public Scheduling(File file){

	}

	private void readData(){
		resources = new ArrayList<>();
		unassignedJobs = new ArrayList<>();
		jobStack = new Stack<>();

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
	
	public static void main(String[] args){
		
	}
}







