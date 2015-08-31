package scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Job {
	private final int startTime;
	private final int jobTime;

	public Job(int start, int time){
		this.startTime = start;
		this.jobTime = time;
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

	public void addJob(Job job){
		jobs.add(job);
	}

	public int getCurrentEndTime(){
		return currentEndTime;
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
	
	public Scheduling(File file){

	}

	private void readData(){

	}

	private void solve(){
		resources = new ArrayList<>();
		

	}
}







