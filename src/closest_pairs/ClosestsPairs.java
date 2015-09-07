package closest_pairs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import javafx.util.Pair;
import scheduling.Scheduling;

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
	}

	private List<Point> pxOrig = new ArrayList<>();
	private List<Point> pyOrig = new ArrayList<>();



	public ClosestsPairs(String path) throws FileNotFoundException{
		this(new File(path));
	}

	public ClosestsPairs(File f) throws FileNotFoundException{
		readData(f);
	}

	private void readData(File file) throws FileNotFoundException{
		/*Point p1 = new Point("far",0,0);
		Point p2 = new Point("romeo",0,10);
		Point p3 = new Point("juliet",0,11);
		Point p4 = new Point("far",0,20);

		pxOrig.add(p1);
		pxOrig.add(p2);
		pxOrig.add(p3);
		pxOrig.add(p4);

		pyOrig.add(p1);
		pyOrig.add(p2);
		pyOrig.add(p3);
		pyOrig.add(p4);*/

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
				System.out.println("Adding: " + label + " (" + dx + "; " + dy + ")");
			} catch (NumberFormatException e){
				System.out.println("Ignoring: " + s);
				//Well... this line didn't parse, so we'll just ignore it...
				continue;
			}			
		} while (scan.hasNextLine());

		scan.close();
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

		return closestsPair;
	}

	private Pair<Point, Point> closestsPairRec(List<Point> px, List<Point> py){
		// n^2 solution for n <= 3 (Is constant O(3^2))
		if(px.size() <= 3){
			Point pA = null;
			Point pB = null;
			double distAB = Double.MAX_VALUE;
			for(int i=0; i<px.size(); i++){
				for(int j=0; j<py.size(); j++){
					if(i==j)
						continue;

					Point p1 = px.get(i);
					Point p2 = px.get(j);

					double dist = p1.getDistance(p2);
					if(dist < distAB){
						pA = p1;
						pB = p2;
					}
				}
			}

			return new Pair<Point,Point>(pA, pB);
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
				Qy.add(py.get(i));
			} else {
				Rx.add(px.get(i));
				Ry.add(py.get(i));
			}
		}

		Pair<Point, Point> leftPair = closestsPairRec(Qx, Qy);
		Pair<Point, Point> rightPair = closestsPairRec(Rx, Ry);


		double leftDist = leftPair.getKey().getDistance(leftPair.getValue());
		double rightDist = rightPair.getKey().getDistance(rightPair.getValue());
		double delta = Math.min(leftDist, rightDist);

		double L = Qx.get(Qx.size()-1).getX();

		List<Point> S = new ArrayList<>();
		for(Point p : py)
			if(Math.abs(L-p.getX()) < delta)
				S.add(p);

		//Closest pair within the 15 positions
		Pair<Point, Point> closestWithinDelta = null;
		double closestWithinDeltaDist = Double.MAX_VALUE;
		for(int i=0; i<S.size(); i++){
			for(int j=i+1; j<i+(15+1); j++){
				if(j >= S.size())
					break;

				double d = S.get(i).getDistance(S.get(j));
				if(d<closestWithinDeltaDist){
					closestWithinDeltaDist = d;
					closestWithinDelta = new Pair<Point,Point>(S.get(i), S.get(j));
				}
			}
		}

		if(closestWithinDeltaDist < delta)
			return closestWithinDelta;
		else if(leftDist < rightDist)
			return leftPair;
		else
			return rightPair;
	}





	public static void main(String[] args) throws Exception {
		if(args.length == 0){
			String input = "input/closest_pairs";
			File folder = new File(input);
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().contains("out"))
					continue;

				ClosestsPairs cp = new ClosestsPairs(input + "/" + file.getName());
				cp.solve();
				//cp.printSolution();
				//System.out.print(file.getName() + " is ok? ");
				File outputFile = new File(input + "/" + file.getName().replaceAll("\\.in", ".out"));
				//		System.out.println(cp.compareResult(outputFile));
			} 
		} else{
			String filePath = args[0];
			ClosestsPairs cp = new ClosestsPairs(filePath);
			cp.solve();
			//cp.printSolution();
			//System.out.print(filePath + " is ok? ");
			//	System.out.println(cp.compareResult(new File(filePath.replaceAll("\\.in", ".out"))));
		}

	}
}
