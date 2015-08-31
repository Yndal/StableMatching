package scheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Job {
	private final int id;
	private final int startTime;
	private final int jobTime;

	public Job(int start, int time, int id){
		this.startTime = start;
		this.jobTime = time;
		this.id = id;
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
	
	public Scheduling(){

	}

	private void readData(File file) throws FileNotFoundException{
		Scanner scan = new Scanner(file);
		int n = scan.nextInt();
		scan.nextLine();

		for (int i=0; i<n; i++) {
			int start = scan.nextInt();
			int end = scan.nextInt();
			pq.add(new Job(start, end, i));
		}
	}

	private void solve(){
		resources = new ArrayList<>();

	}

	public static void main(String args[]) throws Exception{
		Scheduling s = new Scheduling();
		File f = new File("input/scheduling/ip-1.in");
		s.readData(f);

	}
}
