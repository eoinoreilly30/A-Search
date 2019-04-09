package pathFinder;

import java.util.ArrayList;


public class JarvisMarch {
	
	public static Polygon2D findConvexHull(Polygon2D polygon){
		
		ArrayList<Point2D> convexHull = new ArrayList<Point2D>();
		Point2D[] points = polygon.asPointsArray();
		 
		Point2D initialPoint = findInitialPoint(points);
		convexHull.add(initialPoint);
		
		Point2D currentPoint = initialPoint;
		Point2D candidateHullPoint = points[0];

		while(convexHull.size() <= points.length){
			
			for(int i=0; i<points.length; i++){
				
				if (points[i] == currentPoint) {
	                continue;
	            }
				
				double crossProduct = Point2D.turningDirection(currentPoint, points[i], candidateHullPoint);
				
				if(crossProduct < 0){
					candidateHullPoint = points[i];
				}
				
				else if(crossProduct == 0){
					// only choose points[i] if it is further from current point, otherwise skip this point
					if(currentPoint.distanceBetween(candidateHullPoint) < currentPoint.distanceBetween(points[i])) {
                        candidateHullPoint = points[i];
					}
				}
			}
			
			if (candidateHullPoint.getX() == initialPoint.getX() && candidateHullPoint.getY() == initialPoint.getY()) {
                break;
            }
			
			convexHull.add(candidateHullPoint);
			currentPoint = candidateHullPoint;
		}

		// converting to suitable return type
		Point2D[] convexHullAsArray = new Point2D[convexHull.size()];
		for(int j=0; j<convexHull.size(); j++){
			convexHullAsArray[j] = convexHull.get(j);
		}
		
		return new Polygon2D(convexHullAsArray);
	}
	
	private static Point2D findInitialPoint(Point2D[] points){
		double minX = Double.POSITIVE_INFINITY; 
		double minY = Double.POSITIVE_INFINITY;
		double tmpX = 0;
		double tmpY = 0;
		
		// find lowest y value
		for(int i=0; i<points.length; i++){
			tmpY = points[i].getY();
			if(tmpY < minY){
				minY = tmpY;
				minX = points[i].getX();
			}
		}
		
		// find lowest x point with this lowest y value
		for(int i=0; i<points.length; i++){
			if(points[i].getY() == minY){
				tmpX = points[i].getX();
				if(tmpX < minX){
					minX = tmpX;
					minY = points[i].getY();
				}
			}
		}
		
		Point2D initialPoint = new Point2D(minX, minY);
		return initialPoint;
	}
	
	public static void main(String args[]){
		ShapeMap sm0 = new ShapeMap("TEST-MAP-1.txt");
		
		ArrayList<Polygon2D> convexHulls = new ArrayList<Polygon2D>();
		
		for(Polygon2D p : sm0){
			convexHulls.add(findConvexHull(p));
		}
		
		ShapeMap smConvexHulls = new ShapeMap(convexHulls, sm0.sourcePoint(), sm0.destinationPoint());
		
		smConvexHulls.draw();
		for(Polygon2D p : smConvexHulls){
			System.out.println(p);
		}
	}
}
