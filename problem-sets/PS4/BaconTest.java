import java.util.*;

/**
 * Test file for the Kevin Bacon game
 * Creates a graph and tests GraphLibrary methods
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 *
 */
public class BaconTest {
	
	public static void main(String[] args) {
		Graph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
		
		// set up the graph
		graph.insertVertex("Kevin Bacon");
		graph.insertVertex("Alice");
		graph.insertVertex("Bob");
		graph.insertVertex("Charlie");
		graph.insertVertex("Dartmouth");
		
		graph.insertVertex("Nobody");
		graph.insertVertex("Nobody's friend");
		
		Set<String> set1 = new HashSet<String>();
		set1.add("A Movie");
		graph.insertUndirected("Kevin Bacon", "Bob", set1);
		
		Set<String> set2 = new HashSet<String>();
		set2.add("A Movie");
		graph.insertUndirected("Alice", "Bob", set2);
		
		Set<String> set3 = new HashSet<String>();
		set3.add("A Movie");
		set3.add("E Movie");
		graph.insertUndirected("Kevin Bacon", "Alice", set3);
		
		Set<String> set4 = new HashSet<String>();
		set4.add("D Movie");
		graph.insertUndirected("Alice", "Charlie", set4);
		
		Set<String> set5 = new HashSet<String>();
		set5.add("C Movie");
		graph.insertUndirected("Charlie", "Bob", set5);
		
		Set<String> set6 = new HashSet<String>();
		set6.add("B Movie");
		graph.insertUndirected("Charlie", "Dartmouth", set6);
		
		Set<String> set7 = new HashSet<String>();
		set2.add("F Movie");
		graph.insertUndirected("Nobody", "Nobody's friend", set7);
		
		// print the graph to prove that it worked
		System.out.println("Print initial graph:");
		System.out.println(graph + "\n");
		
		// do a bfs with Kevin Bacon as the center of the universe
		Graph<String, Set<String>> newGraph = GraphLibrary.bfs(graph, "Kevin Bacon");
		System.out.println("Print graph centered around Kevin Bacon:");
		System.out.println(newGraph + "\n");
		
		// do a bfs with Bob as the center of the universe
		newGraph = GraphLibrary.bfs(graph, "Bob");
		System.out.println("Print graph centered around Bob:");
		System.out.println(newGraph + "\n");
		
		// do a bfs with Nobody as the center of the universe
		newGraph = GraphLibrary.bfs(graph, "Nobody");
		System.out.println("Print graph centered around Nobody:");
		System.out.println(newGraph + "\n");
		
		// reset graph as centered around Kevin Bacon
		newGraph = GraphLibrary.bfs(graph, "Kevin Bacon");
				
		// get Charlie's path to KB
		List<String> cList = GraphLibrary.getPath(newGraph, "Charlie");
		
		System.out.println(cList.get(0) + "'s number is " + (cList.size()-1));
		
		for (int i=0; i<cList.size() -1; i++) {
			System.out.println(cList.get(i) + " appeared in " + newGraph.getLabel(cList.get(i), cList.get(i+1)) + " with " + cList.get(i+1));
		}
		
		// find missing vertices in graph centered around Kevin Bacon
		System.out.println("\nMissing Vertices:");
		System.out.println(GraphLibrary.missingVertices(graph, newGraph));
		
		// find average separation of tree
		System.out.println("\nAverage Separation:");
		System.out.println("avg separation is " + GraphLibrary.averageSeparation(newGraph, "Kevin Bacon"));		
	}

}
