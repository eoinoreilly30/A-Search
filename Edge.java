package pathFinder;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.princeton.cs.introcs.StdDraw;

public class Edge {
	
	private Node n1;
	private Node n2;
	
	// *************
	// Constructor
	// *************
	public Edge(Node n1, Node n2){
		this.n1 = n1;
		this.n2 = n2;
	}
	
	// *************************************************************
	// Methods to determine if an edge is intersecting another edge
	// *************************************************************
	public boolean isIntersectingAnyEdge(Graph graph){
		ArrayList<ArrayList<Edge>> edges = graph.getEdgeArray();
		
		for(int i=0; i<edges.size(); i++){
			for(int j=0; j<edges.get(i).size(); j++){	
				if(doIntersect(this, edges.get(i).get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean doIntersect(Edge e1, Edge e2){
		if(e1.isConnectedTo(e2)) return false;
		
		return Line2D.Double.linesIntersect(e1.getN1().getX(), e1.getN1().getY(), e1.getN2().getX(), e1.getN2().getY(), 
				e2.getN1().getX(), e2.getN1().getY(), e2.getN2().getX(), e2.getN2().getY());
	} 
	
	private boolean isConnectedTo(Edge e){
		if(this.getN1().isSameNode(e.getN1())
			|| this.getN1().isSameNode(e.getN2())
			|| this.getN2().isSameNode(e.getN1())
			|| this.getN2().isSameNode(e.getN2())) 
				return true;
		
		return false;
	}
	
	// ********************
	// Getters and setters
	// ********************
	public Node getN1(){
		return this.n1;
	}
	
	public Node getN2(){
		return this.n2;
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
		
		// test edges
		Node n1 = new Node(new Point2D(0.1, 0.5));
		Node n2 = new Node(new Point2D(0.4, 0.9));
		Edge e1 = new Edge(n1, n2);
		
		Node n3 = new Node(new Point2D(0.2, 0.9));
		Node n4 = new Node(new Point2D(0.3, 0.5));
		Edge e2 = new Edge(n3, n4);
		
		testIsIntersecting(e1, e2);
		
		// test edges
		Node n5 = new Node(new Point2D(0.1, 0.1));
		Node n6 = new Node(new Point2D(0.4, 0.4));
		Edge e3 = new Edge(n5, n6);
		
		Node n7 = new Node(new Point2D(0.2, 0.5));
		Node n8 = new Node(new Point2D(0.3, 0.9));
		Edge e4 = new Edge(n7, n8);
		
		//testIsIntersecting(e3, e4);
		
		// test edges
		Node n9 = new Node(new Point2D(0.1, 0.5));
		Node n10 = new Node(new Point2D(0.4, 0.9));
		Edge e5 = new Edge(n9, n10);
		
		Node n11 = new Node(new Point2D(0.1, 0.5));
		Node n12 = new Node(new Point2D(0.3, 0.95));
		Edge e6 = new Edge(n11, n12);
		
		//testIsIntersecting(e5, e6);
	}
	
	private static void testIsIntersecting(Edge e1, Edge e2) {
		
		boolean intersecting = doIntersect(e1, e2);
		
		// illustrate
		StdDraw.setCanvasSize(900, 800);
		StdDraw.setXscale(0.0, 1.2);
		StdDraw.setYscale(0.0, 1.2);
		
		//draw e1
		StdDraw.setPenColor(Color.BLUE);
		StdDraw.filledCircle(e1.getN1().getX(), e1.getN1().getY(), 0.009);
		StdDraw.filledCircle(e1.getN2().getX(), e1.getN2().getY(), 0.009);
		StdDraw.setPenColor(Color.ORANGE);
		StdDraw.line(e1.getN1().getX(), e1.getN1().getY(), e1.getN2().getX(), e1.getN2().getY());
		
		//draw e2
		StdDraw.setPenColor(Color.BLUE);
		StdDraw.filledCircle(e2.getN1().getX(), e2.getN1().getY(), 0.009);
		StdDraw.filledCircle(e2.getN2().getX(), e2.getN2().getY(), 0.009);
		StdDraw.setPenColor(Color.ORANGE);
		StdDraw.line(e2.getN1().getX(), e2.getN1().getY(), e2.getN2().getX(), e2.getN2().getY());
		
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		
		if(intersecting){
			StdDraw.text(0.2, 1.1, "Edges are intersecting");
		}
		
		else{
			StdDraw.text(0.2, 1.1, "Edges not intersecting");
		}
	}
}
