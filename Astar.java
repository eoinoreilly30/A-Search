package pathFinder;

import java.awt.*;
import java.util.*;

import edu.princeton.cs.introcs.StdDraw;

public class Astar {
	
	private ShapeMap originalMap;
	private ShapeMap convexHullMap;
	private Graph graph;
	private Node source;
	private Node dest;
	private ArrayList<Node> optimalRoute;
	private double lengthOfPath;
	
	private ArrayList<Node> remaining;
	private ArrayList<Node> visited;
	private Node current;
	private double neighbourCost;

	public Astar(ShapeMap sm, boolean animate){
		
		// initialise variables
		this.remaining = new ArrayList<Node>();
		this.visited = new ArrayList<Node>();
		this.optimalRoute = new ArrayList<Node>();
		this.lengthOfPath = 0;
		this.originalMap = sm;
		
		// create convexHullMap
		ArrayList<Polygon2D> polygons = new ArrayList<Polygon2D>();
		for(Polygon2D p : sm){
			
			// do JarvisMarch
			polygons.add(JarvisMarch.findConvexHull(p));
		}
		
		this.convexHullMap = new ShapeMap(polygons, sm.sourcePoint(), sm.destinationPoint());
		
		// create graph
		this.graph = new Graph(this.convexHullMap);
		this.source = graph.getSrc();
		this.dest = graph.getDest();

		// do A* search
		final long startTime = System.nanoTime();
		
		if(animate){
			this.optimalRoute = computeAstarWithAnimation();
		}
		
		else {
			this.optimalRoute = computeAstar();
		}
		
		final double runTime = (System.nanoTime() - startTime);
		final double runTimeMs = runTime*0.000001;
		
		System.out.println("Execution time: " + runTimeMs + " ms");
	}
	
	private ArrayList<Node> computeAstar(){
			
		source.computeHnValue(dest);
		source.computeFnValue();
		remaining.add(source);

		while(!remaining.isEmpty()){
			
			// get the next node to be expanded
			current = getLowestFn();
			remaining.remove(current);
			
			// if we have reached destination, exit
			if(current.isSameNode(dest)) return getPath(current); // traverse back up through the tree

			// find the next allowed moves
			ArrayList<Node> possibleMoves = current.getConnectedNodes();
			
			for(Node nextPotentialNode : possibleMoves){
				
				// calculate the cost of getting to this potential node
				neighbourCost = current.getGnValue() + current.distanceBetween(nextPotentialNode);
				
				// if already visited, skip this node
				if(visited.contains(nextPotentialNode)) continue;
				
				// add the node to the list of unexplored nodes
				if(!remaining.contains(nextPotentialNode)) remaining.add(nextPotentialNode);
				
				// if the cost of getting to this node is greater than the nodes current G(n) value, skip the node
				else if(neighbourCost > nextPotentialNode.getGnValue()) continue;
				
				// set the parent node to the current node
				nextPotentialNode.setParentNode(current);
				
				// update values
				nextPotentialNode.computeGnValue(current);
				nextPotentialNode.computeHnValue(dest);
				nextPotentialNode.computeFnValue();
			}
			
			// mark the current node as visited
			visited.add(current);			
		}
		
		// temporary return value
		return new ArrayList<Node>();
	}
	
	private ArrayList<Node> computeAstarWithAnimation() {
		
		StdDraw.setCanvasSize(900, 800);
		StdDraw.setXscale(0.0, 1.2);
		StdDraw.setYscale(0.0, 1.2);

		StdDraw.setFont(new Font("Arial", Font.PLAIN, 12));
		StdDraw.setPenColor(Color.GREEN);
		StdDraw.filledCircle(source.getX(), source.getY(), 0.009);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(source.getX(), source.getY()-0.03, "START");
		StdDraw.setPenColor(Color.RED);
		StdDraw.filledCircle(dest.getX(), dest.getY(), 0.009);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(dest.getX(), dest.getY()+0.02, "END");

		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		StdDraw.text(0.2, 1.1, "Original Shapes");
		originalMap.drawFilled();
		StdDraw.pause(3000);
		
		StdDraw.setPenColor(Color.WHITE);
		StdDraw.filledRectangle(0.2, 1.1, 0.4, 0.1);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(0.2, 1.1, "With Convex Hulls");
		convexHullMap.draw();
		StdDraw.pause(3000);
		
		StdDraw.setPenColor(Color.WHITE);
		StdDraw.filledRectangle(0.2, 1.1, 0.4, 0.1);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		StdDraw.text(0.3, 1.1, "Create Spanning Tree");
		StdDraw.pause(1000);
			
		source.computeFnValue();
		remaining.add(source);

		while(!remaining.isEmpty()){
			
			current = getLowestFn();
			
			// animate explore
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.filledCircle(current.getX(), current.getY(), 0.005);
			StdDraw.setPenColor(Color.ORANGE);
			StdDraw.line(current.getX(), current.getY(), current.getParentNode().getX(), current.getParentNode().getY());
			StdDraw.pause(300);
			
			if(current.isSameNode(dest)) return getPathWithAnimation(current);
			
			ArrayList<Node> possibleMoves = current.getConnectedNodes();
			
			for(Node nextPotentialNode : possibleMoves){
				
				if(visited.contains(nextPotentialNode)) continue;

				neighbourCost = current.getGnValue() + current.distanceBetween(nextPotentialNode);
				
				if(!remaining.contains(nextPotentialNode)) remaining.add(nextPotentialNode);
				
				else if(neighbourCost >= nextPotentialNode.getGnValue()) continue;
				
				nextPotentialNode.setParentNode(current);
				nextPotentialNode.computeGnValue(current);
				nextPotentialNode.computeHnValue(dest);
				nextPotentialNode.computeFnValue();
			}
			
			visited.add(current);
			remaining.remove(current);
		}
		
		// temporary return value
		return new ArrayList<Node>();
	}
	
	private ArrayList<Node> getPath(Node currentNode) {
        this.optimalRoute.add(currentNode);
        
        while (!currentNode.isSameNode(this.source)) {
        	this.optimalRoute.add(currentNode.getParentNode());
            this.lengthOfPath += currentNode.distanceBetween(currentNode.getParentNode());
            currentNode = currentNode.getParentNode();
        }
        
        Collections.reverse(this.optimalRoute);
        return this.optimalRoute;
    }
	
	private ArrayList<Node> getPathWithAnimation(Node currentNode) {
		this.optimalRoute.add(currentNode);
        
        StdDraw.setPenColor(Color.WHITE);
		StdDraw.filledRectangle(0.2, 1.1, 0.4, 0.1);
		StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		StdDraw.text(0.3, 1.1, "Destination Found!");
		StdDraw.pause(3000);
		
		StdDraw.setPenColor(Color.WHITE);
		StdDraw.filledRectangle(0.2, 1.1, 0.4, 0.1);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(0.35, 1.1, "Traversing back up through tree");
		StdDraw.pause(1500);
		
        StdDraw.pause(500);
        StdDraw.setPenColor(Color.GREEN);
		StdDraw.filledCircle(currentNode.getX(), currentNode.getY(), 0.005);
		StdDraw.pause(500);
        
        while (!currentNode.isSameNode(this.source)) {
        	this.optimalRoute.add(currentNode.getParentNode());
            this.lengthOfPath += currentNode.distanceBetween(currentNode.getParentNode());
            
            StdDraw.setPenColor(Color.BLACK);
    		StdDraw.line(currentNode.getX(), currentNode.getY(), currentNode.getParentNode().getX(), currentNode.getParentNode().getY());
    		StdDraw.pause(500);
    		
            currentNode = currentNode.getParentNode();
        }
        
        Collections.reverse(this.optimalRoute);
        return this.optimalRoute;
    }
		
	private Node getLowestFn(){
		Node lowestFnNode = new Node(new Point2D(100, 100));
		
		for(int i=0; i<remaining.size(); i++){
			if(remaining.get(i).getFnValue() < lowestFnNode.getFnValue()){
				lowestFnNode = remaining.get(i);
			}
		}
		return lowestFnNode;
	}

	public static void main(String args[]) {
		
		ShapeMap sm = new ShapeMap(args[0]);
		boolean animate = false;
		
		if(args.length > 1){
			if(args[1].equals("-V")) animate = true;
		}
		
		
		// do Astar
		Astar aStar = new Astar(sm, animate);
		ArrayList<Node> optimalRoute = aStar.optimalRoute;
		
		System.out.println("Length: " + aStar.lengthOfPath);
		
		// draw solution
		StdDraw.setCanvasSize(900, 800);
		StdDraw.setXscale(0.0, 1.2);
		StdDraw.setYscale(0.0, 1.2);
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		StdDraw.text(0.2, 1.1, "Found Path!");
		sm.drawFilled();
		aStar.convexHullMap.draw();

		StdDraw.setPenRadius(0.005);
		
		for(int i=0; i<optimalRoute.size()-1; i++){
			StdDraw.setPenColor(Color.ORANGE);
			StdDraw.filledCircle(optimalRoute.get(i).getX(), optimalRoute.get(i).getY(), 0.008);
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.line(optimalRoute.get(i).getX(), optimalRoute.get(i).getY(), optimalRoute.get(i+1).getX(), optimalRoute.get(i+1).getY());
		}
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 12));
		StdDraw.setPenColor(Color.GREEN);
		StdDraw.filledCircle(aStar.source.getX(), aStar.source.getY(), 0.009);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(aStar.source.getX(), aStar.source.getY()-0.03, "START");
		StdDraw.setPenColor(Color.RED);
		StdDraw.filledCircle(aStar.dest.getX(), aStar.dest.getY(), 0.009);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(aStar.dest.getX(), aStar.dest.getY()+0.02, "END");
	}
}