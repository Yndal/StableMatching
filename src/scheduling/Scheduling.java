package scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
		
	}
	
	public int getCurrentEndTime(){
		return currentEndTime;
	}
	
	
}

public class Scheduling {

	public Scheduling(File file){
		
	}
	
	private void readData(){
		
	}
	
	private void solve(){
		
	}
}







