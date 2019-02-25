import java.util.*;

/**
 * Library for graph analysis
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018 -- filled in methods
 * 
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g		graph to walk on
	 * @param start	initial vertex (assumed to be in graph)
	 * @param steps	max number of steps
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) {
		// create list of path to be returned
		List<V> list = new ArrayList<V>();
		
		// create stack to keep track of path and add start
		Stack<V> stack = new Stack<V>();
		stack.push(start);
		
		// initialize count to keep track of how many nodes added to list
		int count = 0;
		
		// continue looping until hit the desired number of steps or hit a node with no out neighbors
		while(count <= steps && !stack.isEmpty()) {
			// pop from the stack and add it to the list
			V u = stack.pop();
			list.add(u);
			
			// create iterator object of out neighbors and variable to keep count
			int neighborCount = 0;
			Iterator<V> iterator = g.outNeighbors(u).iterator();
			
			// loop through neighbors to determine out how many there are
			for (Iterator<V> i = iterator; i.hasNext();) {
				neighborCount++;
				i.next();
			}
			
			// calculate random number based on number of neighbors
			int random = (int) (Math.random() * neighborCount);
			
			// reset iterator and neighborCount for second loop
			neighborCount = 0;
			iterator = g.outNeighbors(u).iterator();
			
			// loop through neighbors again, only adding the random one			
			for (Iterator<V> i = iterator; i.hasNext();) {
				V key = i.next();
				if (neighborCount == random) {
					stack.push(key);
				}
				neighborCount++;
			}
			count++;
		}
		
		return list;
	}
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		// create list of vertices to be returned
		List<V> list = new ArrayList<V>();
		
		// instantiate iterator of the vertices
		Iterator<V> iterator = g.vertices().iterator();
		
		// loop through vertices, adding each to the list
		for (Iterator<V> i = iterator; i.hasNext();) {
			list.add(i.next());
		}
		
		// sort the list by passing a GraphComparatorSA7 object with a reference to the graph
		list.sort(new InDegreeGraphComparator(g));
		
		return list;
	}	
	
	/**
	 * Orders vertices in decreasing order by their out-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByOutDegree(Graph<V,E> g) {
		// create list of vertices to be returned
		List<V> list = new ArrayList<V>();
		
		// instantiate iterator of the vertices
		Iterator<V> iterator = g.vertices().iterator();
		
		// loop through vertices, adding each to the list
		for (Iterator<V> i = iterator; i.hasNext();) {
			list.add(i.next());
		}
		
		// sort the list by passing a GraphComparatorSA7 object with a reference to the graph
		list.sort(new OutDegreeGraphComparator(g));
		
		return list;
	}	
}
