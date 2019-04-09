// Point2D.java
// C.McArdle, DCU, 2018

package pathFinder;

import edu.princeton.cs.introcs.StdDraw;

public class Point2D {

	private final double x;
	private final double y;
	
	public Point2D(double x, double y) { // constructor
		this.x = x;
		this.y = y;
	}

	public Point2D(Point2D p) { // copy constructor
		if (p == null)  System.out.println("Point2D(): null point!");
		x = p.getX();
		y = p.getY();
	}

	public double getX() { return x; }

	public double getY() { return y; }

	public static double turningDirection(Point2D center, Point2D a, Point2D b){
		double ax = (a.getX()-center.getX());
		double ay = (a.getY()-center.getY());
		double bx = (b.getX()-center.getX());
		double by = (b.getY()-center.getY());

		return ((ax*by)-(bx*ay));
	}
	
	public double distanceBetween(Point2D p1) {
		return Math.sqrt(Math.pow((this.getX()-p1.getX()), 2) + Math.pow((this.getY()-p1.getY()), 2));
	}

	@Override
	//edit
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

	public void draw() {
		StdDraw.point(x, y);
	}

}
