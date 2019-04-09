package pathFinder;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.lang.Math;

import edu.princeton.cs.introcs.StdDraw;

public class Node extends Point2D {
	
	private Polygon2D parentPolygon;
	private ArrayList<Node> connectedNodes = new ArrayList<Node>();
	
	//A* data
	private Node parentNode;
	private double fnValue = Double.POSITIVE_INFINITY;
	private double hnValue = 0;
	private double gnValue = 0;
	
	
	// ******************
	// Constructors
	// ******************
	public Node(Point2D p, Polygon2D parentPolygon){
		super(p);
		this.parentPolygon = parentPolygon;
		this.parentNode = this;
	}
	
	public Node(Point2D p){
		super(p);
		this.parentNode = this;
	}
	
	// ***********************************************************
	// Methods to determine if a node is an allowed move
	// ***********************************************************
	public boolean isPossibleMove(Node candidate, Graph graph){
		if(this.isDirectlyConnectedTo(candidate)){
			return true;
		}
		
		if(this.isMemberOfPolygon(candidate.getParentPolygon())){
			return false;
		}
		
		Edge thisEdge = new Edge(this, candidate);
		if(thisEdge.isIntersectingAnyEdge(graph)){
			return false;
		}
		
		return true;
	}
	
	private boolean isDirectlyConnectedTo(Node n){
		for(int i=0; i<this.connectedNodes.size(); i++){
			if(n.isSameNode(connectedNodes.get(i))) return true;
		}
		return false;
	}
	
	public boolean isSameNode(Node n){
		if((this.getX() == n.getX()) && (this.getY() == n.getY())){
			return true;
		}
		return false;
	}
	
	private boolean isMemberOfPolygon(Polygon2D parentPolygon){
		if(parentPolygon == null) return false;
		
		for(int i=0; i<parentPolygon.size(); i++){
			if(this.isSameNode(new Node(parentPolygon.getIndex(i)))){
				return true;
			}
		}
		return false;
	}
	
	public double distanceBetween(Node n){
		return Math.sqrt(Math.pow((this.getX()-n.getX()), 2) + Math.pow((this.getY()-n.getY()), 2));
	}
	
	// *************************
	// A* algorithm methods
	// *************************
	public void computeFnValue(){
		this.fnValue = this.gnValue + this.hnValue;
	}
	
	public void computeGnValue(Node n){
		this.gnValue = this.distanceBetween(n) + n.getGnValue();
	}
	
	public void computeHnValue(Node dest){
		this.hnValue = this.distanceBetween(dest);
	}
	
	// **********************
	// Getters and setters
	// **********************
	public double getFnValue(){
		return fnValue;
	}
	
	public double getGnValue(){
	    return gnValue;
	}
	
	public double getHnValue(){
		return hnValue;
	}
	
	public void addDirectlyConnectedNode(Node n){
		this.connectedNodes.add(n);
	}
	
	public void addConnectedNode(ArrayList<Node> nodes){
		for(Node n : nodes) {
			this.connectedNodes.add(n);
		}	
	}
	
	public ArrayList<Node> getConnectedNodes(){
		return this.connectedNodes;
	}
	
	public void setParentNode(Node n){
		this.parentNode = n;
	}
	
	public Node getParentNode(){
		return parentNode;
	}
	
	public Polygon2D getParentPolygon(){
		return this.parentPolygon;
	}
	
	// **************************
	// Main - Used for testing
	// **************************
	public static void main(String args[]){
		ShapeMap sm = new ShapeMap("TEST-MAP-1.TXT");
		
		ArrayList<Polygon2D> polygons = new ArrayList<Polygon2D>();
		
		for(Polygon2D p : sm){
			polygons.add(JarvisMarch.findConvexHull(p));
		}
		
		ShapeMap convexHullMap = new ShapeMap(polygons, sm.sourcePoint(), sm.destinationPoint());
		Graph graph = new Graph(convexHullMap);
		
		// test nodes
		Node n1 = graph.getNodeArray().get(0).get(0);
		Node n2 = graph.getNodeArray().get(0).get(1);
		
		testIsPossibleMove(n1, n2, graph, convexHullMap);
		
		// test nodes
		Node n3 = graph.getNodeArray().get(0).get(0);
		Node n4 = graph.getNodeArray().get(0).get(3);
		
		testIsPossibleMove(n3, n4, graph, convexHullMap);
		
		// test nodes
		Node n5 = graph.getNodeArray().get(0).get(0);
		Node n6 = graph.getNodeArray().get(2).get(1);
		
		testIsPossibleMove(n5, n6, graph, convexHullMap);
	}
	
	private static void testIsPossibleMove(Node n1, Node n2, Graph g, ShapeMap convexHullMap) {
		
		boolean possibleMove = n1.isPossibleMove(n2, g);
		
		// illustrate
		StdDraw.setCanvasSize(900, 800);
		StdDraw.setXscale(0.0, 1.2);
		StdDraw.setYscale(0.0, 1.2);
		convexHullMap.draw();
		StdDraw.setPenColor(Color.BLUE);
		StdDraw.filledCircle(n1.getX(), n1.getY(), 0.009);
		StdDraw.filledCircle(n2.getX(), n2.getY(), 0.009);
		StdDraw.setPenColor(Color.ORANGE);
		StdDraw.line(n1.getX(), n1.getY(), n2.getX(), n2.getY());
		
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		if(possibleMove){
			StdDraw.text(0.2, 1.1, "Allowed Move");
		}
		
		else{
			StdDraw.text(0.2, 1.1, "Not Allowed Move");
		}
	}
}
