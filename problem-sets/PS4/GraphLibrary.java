import java.util.*;

/**
 * Library for graph analysis
 * Create methods to run a breadth-first search on a graph, get a path from a given node to the center (root),
 * get a list of missing vertices between two graphs, and find the average distance-from-root of all nodes in a tree
 *  
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018 -- filled in methods
 */
public class GraphLibrary {
	
	// breadth-first search to find the shortest path tree for a current center of the universe
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
		// create map to backtrack paths, add start node to it
		Graph<V,E> backtrack = new AdjacencyMapGraph<V,E>();
		backtrack.insertVertex(source);
		
		// create set of visited nodes and queue for nodes to visit
		Set<V> visited = new HashSet<V>();
		List<V> queue = new LinkedList<V>();
		
		// mark start node as visited and add to queue
		visited.add(source);
		queue.add(source);
		
		// loop through all neighbors in graph
		while (!queue.isEmpty()) {
			// dequeue a parent node
			V parent = queue.remove(0);
			
			// for each of its neighbors, add to queue and create connection if not already visited
			for (V neighbor : g.outNeighbors(parent)) {
				
				// check if already visited
				if (!visited.contains(neighbor)) {
					
					// mark as visited and add to queue
					visited.add(neighbor);
					queue.add(neighbor);
					
					// add backtrack vertex if doesn't already exist
					if (!backtrack.hasVertex(neighbor)) {
						backtrack.insertVertex(neighbor);
					}
					
					// add parent vertex if doesn't already exist
					if (!backtrack.hasVertex(parent)) {
						backtrack.insertVertex(parent);
					}
					
					// create a directed edge from the child to the parent
					backtrack.insertDirected(neighbor, parent, g.getLabel(neighbor, parent));
				}
			}
		}
		return backtrack;
	}
	
	// given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
		// instantiate list for path and add start to the list
		List<V> path = new ArrayList<V>();
		
		// create instance variable for current node being looked at
		V current = v;
				
		// trek through tree until hit center of universe, adding each step to the path
		while (tree.outDegree(current) != 0) {
			path.add(current);
			
			// update current by getting the only neighbor of the current node
			current = tree.outNeighbors(current).iterator().next();
		}
		
		// add last stop (center of the universe) then return
		path.add(current);
		return path;
	}
	
	// given a graph and a subgraph , determine which vertices are in the graph but not the subgraph
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
		// instantiate set of missing vertices
		Set<V> missing = new HashSet<V>();
		
		// create set of all vertices in the subgraph
		Set<V> subVertices = new HashSet<V>();
		
		// add all vertices in subgraph to the set of its vertices
		for (V v : subgraph.vertices()) {
			subVertices.add(v);
		}
		
		// loop through each vertex in graph
		for (V v : graph.vertices()) {
			// if not in set of subgraph's vertices, add it to the missing
			if (!subVertices.contains(v)) {
				missing.add(v);
			}
		}
		return missing;
	}
	
	// find the average distance-from-root in a shortest path tree
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
		// call helper for number of edges
		double num = counter(tree,root,0);
		
		// calculate number of vertices as all but one
		double verts = tree.numVertices() - 1;
		
		//System.out.println("Number of edges: " + num);
		//System.out.println("Number of vertices: " + verts);
		
		// return avg by dividing - create format so only two digits after deciminal point
		return Double.parseDouble(new java.text.DecimalFormat("0.00").format(num/verts));
	}
	
	public static <V,E> double counter(Graph<V,E> tree, V v, double prev) {
		double sum = prev;
					
		for (V vertex : tree.inNeighbors(v)) {
			sum += counter(tree, vertex, prev+1);
		}
				
		return sum;
	}
}