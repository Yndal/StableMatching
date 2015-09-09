package closest_pairs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import javafx.util.Pair;

public class ClosestsPairs {
	private class Point{
		private final String label;
		private final double x;
		private final double y;

		private Point(String label, double x, double y){
			this.label = label;
			this.x = x;
			this.y = y;
		}

		public double getDistance(Point point){
			return Math.sqrt(Math.pow((this.x-point.x), 2) + Math.pow((this.y-point.y), 2));
		}

		public String getLabel(){
			return label;
		}

		public double getX(){
			return x;
		}

		public double getY(){
			return y; 
		}

		@Override
		public String toString() {
			return label + ": (" + x + "," + y + ")";
		}
	}
	
	private static final double DIST_RESULT_MAX_DELTA = 0.000001;
	
	private HashMap<String, Double> results = new HashMap<>();
	private HashMap<String, Integer> results_points = new HashMap<>();
	private Pair<Point,Point> result;
	private String setName;
	
	private List<Point> pxOrig = new ArrayList<>();
	private List<Point> pyOrig = new ArrayList<>();



	public ClosestsPairs(String resultPath) throws FileNotFoundException{
		this(new File(resultPath));
	}

	public ClosestsPairs(File resultFile) throws FileNotFoundException{
		loadResults(resultFile);
	}
	
	private void loadResults(File file) throws FileNotFoundException{
		results = new HashMap<>();
		
		Scanner scanner = new Scanner(file);
		String s;
		while(scanner.hasNextLine()){
			s = scanner.nextLine();
			
			//Remove ../data/
			s = s.substring(s.lastIndexOf("/")+1);
			String[] ss = s.trim().split(" ");
			
			try{
				String name = ss[0].substring(0, ss[0].indexOf("."));
				int pointsInFile = Integer.valueOf(ss[1]);
				double shortestDistance = Double.valueOf(ss[2]);
				results.put(name, shortestDistance);
				results_points.put(name, pointsInFile);
			} catch (NumberFormatException e){
				//Shhiiaat
				System.out.println("Unable to parse result: " + s);
			}
		}
		scanner.close();
	}

	public void readData(File file) throws FileNotFoundException{
		//Clear potential old data
		result = null;
		setName = null;
		pxOrig.clear();
		pyOrig.clear();
		
		//Get name of set
		String name = file.getName();
		name = name.substring(0, name.indexOf("-"));
		setName = name;
		
		//Read input from file
		Scanner scan = new Scanner(file);
		String s;
		do{
			s = scan.nextLine();
			String[] ss = s.trim().split(" ");
			Queue<String> queue = new LinkedList<>();
			for(String s1 : ss){
				if(!s1.isEmpty())
					queue.add(s1);
			}
			
			if(queue.size() < 3)
				continue;

			String label = queue.poll();
			String x = queue.poll();
			String y = queue.poll();

			try{
				double dx = Double.parseDouble(x);
				double dy = Double.parseDouble(y);

				Point point = new Point(label,dx,dy);
				pxOrig.add(point);
				pyOrig.add(point);
			//	System.out.println("Adding: " + label + " (" + dx + "; " + dy + ")");
			} catch (NumberFormatException e){
			//	System.out.println("Ignoring: " + s);
				//Well... this line didn't parse, so we'll just ignore it...
				continue;
			}			
		} while (scan.hasNextLine());

		scan.close();
		
		/*
		for (Point p : pxOrig){
			System.out.println(p.getX() + ", " + p.getY());
		}
		*/
	}

	//Yes, yes - n², but just to check
	public Pair<Point, Point> solveBruteForce(List<Point> p) {
		Point pA = null;
		Point pB = null;
		double distAB = Double.MAX_VALUE;
		for (int i=0; i< p.size(); i++) {
			for (int j=i+1; j<p.size(); j++) {
				Point p1 = p.get(i);
				Point p2 = p.get(j);
				double dist = p1.getDistance(p2);
				if (dist < distAB) {
					pA = p1;
					pB = p2;
					distAB = dist;
				}
			}
		}
		
		Pair<Point, Point> closestsPair = new Pair<Point, Point>(pA, pB);
		result = closestsPair;
		
		return closestsPair;		
	}
	
	public Pair<Point,Point> solve(){
		Collections.sort(pxOrig, new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				double val = p2.x - p1.x;

				if(val < 0)
					return 1;
				else if(val > 0)
					return -1;
				else 
					return 0;
			}
		});
		Collections.sort(pyOrig, new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				double val = p2.y - p1.y;

				if(val < 0)
					return 1;
				else if(val > 0)
					return -1;
				else 
					return 0;
			}
		});

		Pair<Point, Point> closestsPair = closestsPairRec(pxOrig, pyOrig);
		result = closestsPair;

		return closestsPair;
	}

	private Pair<Point, Point> closestsPairRec(List<Point> px, List<Point> py){
		// n^2 solution for n <= 3 (Is constant O(3^2))
		if(px.size() <= 3){
			return solveBruteForce(px);
		}

		//Partition the plane
		int lineIdx = px.size()/2; //floor(n/2)
		List<Point> Qx = new ArrayList<Point>();
		List<Point> Qy = new ArrayList<Point>();
		List<Point> Rx = new ArrayList<Point>();
		List<Point> Ry = new ArrayList<Point>();
		for (int i = 0; i< px.size(); i++){
			if (i < lineIdx){
				Qx.add(px.get(i));
			} else {
				Rx.add(px.get(i));
			}
		}

		double L = Qx.get(Qx.size()-1).getX();

		for (int i = 0; i< py.size(); i++){
			if (py.get(i).getX() < L){
				Qy.add(py.get(i));
			} else {
				Ry.add(py.get(i));
			}
		}

		Pair<Point, Point> leftPair = closestsPairRec(Qx, Qy);
		Pair<Point, Point> rightPair = closestsPairRec(Rx, Ry);


		double leftDist = leftPair.getKey().getDistance(leftPair.getValue());
		double rightDist = rightPair.getKey().getDistance(rightPair.getValue());
		double delta = Math.min(leftDist, rightDist);


		List<Point> S = new ArrayList<>();
		for(Point p : py)
			if(Math.abs(L-p.getX()) < delta)
				S.add(p);

		//Closest pair within the 15 positions
		Pair<Point, Point> closestWithinDelta = null;
		double closestWithinDeltaDist = delta;
		for(int i=0; i<S.size(); i++){
			for(int j=i+1; j<i+(15+1); j++){
				if(j == S.size()){
					break;
				}

				double d = S.get(i).getDistance(S.get(j));
				if(d<closestWithinDeltaDist){
					closestWithinDeltaDist = d;
					closestWithinDelta = new Pair<Point,Point>(S.get(i), S.get(j));
				}
			}
		}

		if(closestWithinDeltaDist < delta){
			return closestWithinDelta;
		}
		else if(leftDist < rightDist){
			return leftPair;
		}else{
			return rightPair;
		}
	}

	public boolean printResult(){
		System.out.print("Result for " + setName + ": \t");
		if(!results.containsKey(setName)){
			System.out.println("Result not provided...");
			return true;
		}
		
		double actResult = results.get(setName);
		double ourResult = result.getKey().getDistance(result.getValue());
		
		if(Math.abs(actResult - ourResult) < DIST_RESULT_MAX_DELTA){
			//System.out.println("Correct");
			System.out.println("Correctemundo: Dist is " + actResult + " vs " + ourResult + "(Points " + results_points.get(setName) + " vs " + pxOrig.size() + ")" + " Pair was " + result.getKey().getLabel() + " - " + result.getValue().getLabel());
			return true;
		} else {
			System.out.println("Fail: Dist is " + actResult + " vs " + ourResult + "(Points " + results_points.get(setName) + " vs " + pxOrig.size() + ")" + " Pair was " + result.getKey().getLabel() + " - " + result.getValue().getLabel());
			
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		String resultPath = "input/closest_pairs/closest-pair-out.txt";
		ClosestsPairs cp = new ClosestsPairs(resultPath);
		
		//args = new String[]{"d2103-tsp.txt"};
		String input = "input/closest_pairs";
		
		if(args.length == 0){
			File folder = new File(input);
			File[] files = folder.listFiles();
			int failCounter = 0;
			int fileCounter = files.length;
			for(File file : files){
				if(file.getName().contains("out")){
					fileCounter--;
					continue;
				}

				cp.readData(file);//new File(input + "/" + file.getName()));
				cp.solve();
				//cp.solveBruteForce(cp.pxOrig);
				if(!cp.printResult())
					failCounter++;				
			} 
			System.out.println("\nFails: " + failCounter + "/" + fileCounter + "\n(Computations lacking a result are not considered as fails)");
		} else{
			cp.readData(new File(input + "/" + args[0]));
			cp.solve();
			//cp.solveBruteForce(cp.pxOrig);
			cp.printResult();	
		}

	}
	public static String pairToString(Pair<Point, Point> p){
		if (null==p) {
			return "null";
		}
		Point p1 = p.getKey();
		Point p2 = p.getValue();
		return p1.toString() + " - " +p2.toString();
	}
}
