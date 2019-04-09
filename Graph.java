package pathFinder;

import java.awt.Color;
import java.util.ArrayList;

import edu.princeton.cs.introcs.StdDraw;

public class Graph {
	
	// 2D arraylist: outer = polygon, inner = nodes/edges in polygon
	private ArrayList<ArrayList<Node>> nodes = new ArrayList<ArrayList<Node>>();
	private ArrayList<ArrayList<Edge>> edges = new ArrayList<ArrayList<Edge>>();
	
	// to make compatible with 2D array above
	private ArrayList<Node> srcNode = new ArrayList<Node>(1);
	private ArrayList<Node> destNode = new ArrayList<Node>(1);
	
	// *************
	// Constructor
	// *************
	public Graph(ShapeMap sm){
		
		// populate the nodes and edges array
		initialiseNodesAndEdges(sm);
		
		//add source & destination
		srcNode.add(new Node(sm.sourcePoint()));
		destNode.add(new Node(sm.destinationPoint()));
		this.nodes.add(srcNode);
		this.nodes.add(destNode);
		
		// for each node in the map, add its neighbours to 'connectedNodes' in the node class
		initialiseConnectedNodes();
	}
	
	// *******************
	// Constructor helpers
	// *******************	
	private void initialiseNodesAndEdges(ShapeMap sm){
		
		for(Polygon2D currentPolygon : sm){
			
			// create the inner arraylists
			ArrayList<Node> innerNodes = new ArrayList<Node>(currentPolygon.size());
			ArrayList<Edge> innerEdges = new ArrayList<Edge>(currentPolygon.size());
			
			// populate the innerNode array
			for(int i=0; i<currentPolygon.size(); i++){
				innerNodes.add(new Node(currentPolygon.getIndex(i), currentPolygon));
			}
			
			// set the connected nodes and edges for the polygons:
			// connect first node to last point in polygon
			innerNodes.get(0).addDirectlyConnectedNode(new Node(currentPolygon.getIndex(currentPolygon.size()-1), currentPolygon));
			
			// connect forwards and create the corresponding edges
			for(int i=0; i<currentPolygon.size()-1; i++){
				innerNodes.get(i).addDirectlyConnectedNode(new Node(currentPolygon.getIndex(i+1), currentPolygon));
				innerEdges.add(new Edge(innerNodes.get(i), new Node(currentPolygon.getIndex(i+1))));
			}
			
			// connect backwards
			for(int i=1; i<currentPolygon.size(); i++){
				innerNodes.get(i).addDirectlyConnectedNode(new Node(currentPolygon.getIndex(i-1), currentPolygon));	
			}
			
			// connect last node to first point in polygon and create its edge
			innerNodes.get(innerNodes.size()-1).addDirectlyConnectedNode(new Node(currentPolygon.getIndex(0), currentPolygon));
			innerEdges.add(new Edge(innerNodes.get(innerNodes.size()-1), new Node(currentPolygon.getIndex(0))));
			
			this.nodes.add(innerNodes);
			this.edges.add(innerEdges);
		}
	}
	
	private void initialiseConnectedNodes() {
		
		findNeighbourNodes(srcNode.get(0));
		findNeighbourNodes(destNode.get(0));
		
		for(int i=0; i<nodes.size(); i++){
			for(int j=0; j<nodes.get(i).size(); j++){
				findNeighbourNodes(nodes.get(i).get(j));
			}
		}
	}
	
	private void findNeighbourNodes(Node n){
		ArrayList<Node> neighbourNodes = new ArrayList<Node>();
		
		for(int i=0; i<nodes.size(); i++){
			for(int j=0; j<nodes.get(i).size(); j++){
				if(n.isPossibleMove(nodes.get(i).get(j), this)){			
					neighbourNodes.add(nodes.get(i).get(j));
				}
			}
		}
		n.addConnectedNode(neighbourNodes);
	}
	
	// ********************
	// Accessors
	// ********************
	public Node getSrc(){
		return this.srcNode.get(0);
	}
	
	public Node getDest(){
		return destNode.get(0);
	}
	
	public ArrayList<ArrayList<Node>> getNodeArray(){
		return this.nodes;
	}
	
	public ArrayList<ArrayList<Edge>> getEdgeArray(){
		return this.edges;
	}
	
	// **************************
	// Main - Used for testing
	// **************************	
	public static void main(String args[]){
		ShapeMap sm = new ShapeMap("TEST-MAP-0.TXT");
		
		ArrayList<Polygon2D> polygons = new ArrayList<Polygon2D>();
		
		for(Polygon2D p : sm){
			polygons.add(JarvisMarch.findConvexHull(p));
		}
		
		ShapeMap convexHullMap = new ShapeMap(polygons, sm.sourcePoint(), sm.destinationPoint());
		Graph graph = new Graph(convexHullMap);
		
		// illustrate connected nodes
		StdDraw.setCanvasSize(900, 800);
		StdDraw.setXscale(0.0, 1.2);
		StdDraw.setYscale(0.0, 1.2);
		convexHullMap.drawFilled();
		
		ArrayList<ArrayList<Node>> nodes = graph.getNodeArray();
		
		for(int i=0; i<nodes.size(); i++){
			for(int j=0; j<nodes.get(i).size(); j++){
				
				Node currentNode = nodes.get(i).get(j);
				ArrayList<Node> connectedNodes = currentNode.getConnectedNodes();
				
				StdDraw.setPenColor(Color.BLUE);
				StdDraw.filledCircle(currentNode.getX(), currentNode.getY(), 0.005);
				
				for(Node n : connectedNodes){
					StdDraw.setPenColor(Color.ORANGE);
					StdDraw.line(currentNode.getX(), currentNode.getY(), n.getX(), n.getY());
				}
				
			}
		}
	}
}
