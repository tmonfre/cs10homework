/**
 * Test program for graph interface for SA7
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class SA7Test {
	public static void main(String[] args) {
		// instantiate graph
		Graph<String, String> relationships = new AdjacencyMapGraph<String, String>();

		// add to graph
		relationships.insertVertex("A");
		relationships.insertVertex("B");
		relationships.insertVertex("C");
		relationships.insertVertex("D");
		relationships.insertVertex("E");
		relationships.insertDirected("A", "B", "direct");
		relationships.insertDirected("A", "C", "direct");
		relationships.insertDirected("A", "D", "direct");
		relationships.insertDirected("A", "E", "direct");
		relationships.insertDirected("B", "A", "direct");
		relationships.insertDirected("B", "C", "direct");
		relationships.insertDirected("C", "A", "direct");
		relationships.insertDirected("C", "B", "direct");
		relationships.insertDirected("C", "D", "direct");
		relationships.insertDirected("E", "B", "direct");
		relationships.insertDirected("E", "C", "direct");
		
		// print the graph
		System.out.println("The graph:");
		System.out.println(relationships);
		
		// random walk identical 1
		System.out.println("\nRandom Walk (from 'A' with 4 steps):");
		System.out.println(GraphLib.randomWalk(relationships, "A", 4));
		
		// random walk identical 2
		System.out.println("\nRandom Walk (from 'A' with 4 steps):");
		System.out.println(GraphLib.randomWalk(relationships, "A", 4));
		
		// random walk identical 3
		System.out.println("\nRandom Walk (from 'A' with 4 steps):");
		System.out.println(GraphLib.randomWalk(relationships, "A", 4));
		
		// different random walk
		System.out.println("\nRandom Walk (from 'B' with 0 steps):");
		System.out.println(GraphLib.randomWalk(relationships, "B", 0));
		
		// different random walk
		System.out.println("\nRandom Walk (from 'C' with 2 steps):");
		System.out.println(GraphLib.randomWalk(relationships, "C", 2));
		
		// print values of each vertex's in-degree
		System.out.println("\nIn Degree Values:");
		System.out.println("A: " + relationships.inDegree("A"));
		System.out.println("B: " + relationships.inDegree("B"));
		System.out.println("C: " + relationships.inDegree("C"));
		System.out.println("D: " + relationships.inDegree("D"));
		System.out.println("E: " + relationships.inDegree("E"));
		
		// print vertices sorted by in degree
		System.out.println("\nVertices Sorted by In Degree:");
		System.out.println(GraphLib.verticesByInDegree(relationships));
		
		// print values of each vertex's out-degree
		System.out.println("\nOut Degree Values:");
		System.out.println("A: " + relationships.outDegree("A"));
		System.out.println("B: " + relationships.outDegree("B"));
		System.out.println("C: " + relationships.outDegree("C"));
		System.out.println("D: " + relationships.outDegree("D"));
		System.out.println("E: " + relationships.outDegree("E"));
		
		// print vertices sorted by out degree
		System.out.println("\nVertices Sorted by Out Degree:");
		System.out.println(GraphLib.verticesByOutDegree(relationships));
	}
}