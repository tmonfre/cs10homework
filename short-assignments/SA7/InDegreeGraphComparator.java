import java.util.Comparator;

/**
 * comparator class that implements interface Comparator
 * implements compare method to compare in degrees of different nodes in a graph
 * stores generic types - must be instantiated by passing a graph
 * 
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class InDegreeGraphComparator<V, E> implements Comparator<V>{
	// instance variable for generic graph
	public Graph<V, E> g;
	
	// constructor method - pass in graph
	public InDegreeGraphComparator(Graph<V, E> graph) {
		g = graph;
	}
	
	// comparing two nodes in the graph based on in degree
	public int compare(V s1, V s2) {
		return (g.inDegree(s2) - g.inDegree(s1));
	}

}
