import java.io.*;
import java.util.*;

/**
 * This program runs a Kevin Bacon, six-degrees-of-separation game.
 * It reads input files for actors, movies, and movieActors, constructs a graph of connections, and allows for user changes.
 * 
 * User interface:
 * type u <name> to change the current center of the universe to a different actor
 * type p <name> to get a path from <name> to the current center of the universe
 * type i to get a list of all actors with infinite separation from the current center of the universe
 * type d <low> <high> to get a list of all connected actors sorted by degree between high and low
 * type c <num> to get the top (positive <num>) or bottom (negative <num>) centers of the universe sorted by average separation
 * type s <low> <high> to get all actors sorted by non-infinite separation from current center with separation between low and high
 * 
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */

public class Bacon {
	// maps linking actors and movies to their IDs
	private Map<String, String> actors;
	private Map<String, String> movies;
	private Map<String, Set<String>> movieActors;
	
	// general graph and center of universe graph
	private Graph<String, Set<String>> originalGraph;
	private Graph<String, Set<String>> COIGraph;
	
	// name of the center of the universe
	private String centerOfUniverse;
	
	/**
	 * constructor method for class Bacon
	 * instantiates all objects, creates original graph, and creates center of universe graph
	 */
	public Bacon() {
		// instantiate all maps
		actors = new HashMap<String, String>();
		movies = new HashMap<String, String>();
		movieActors = new HashMap<String, Set<String>>();
		
		// instantiate the graph
		originalGraph = new AdjacencyMapGraph<String, Set<String>>();
		
		// fill in the graphs of all actors, movies, and actors in movies		
		createActors();
		createMovies();
		createMovieActors();
		
		// create the original graph
		createOriginalGraph();
				
		// declare the center of universe and create a map around it
		setCenterOfUniverse("Kevin Bacon");
	}
	
	// GETTERS and SETTERS
	
	public String getCenterOfUniverse() {
		return centerOfUniverse;
	}
	
	public Map<String, String> getActors() {
		return actors;
	}
	
	public Map<String, String> getMovies() {
		return movies;
	}
	
	public Map<String, Set<String>> getMovieActors() {
		return movieActors;
	}
	
	public void setCenterOfUniverse(String s) {
		centerOfUniverse = s;
		newCenterOfUniverse(centerOfUniverse);
	}
	
	// HELPER METHODS FOR READING FILES AND CREATING GRAPHS
	
	/**
	 * this method creates the graph by adding a vertex for each actor, then adds an edge for each actor that shared a movie together
	 */
	private void createOriginalGraph() {
		// add vertices
		for (String actorID : actors.keySet()) {
			originalGraph.insertVertex(actors.get(actorID));
		}
			
		// add edges
		// loop over each movie
		for (String movieID : movies.keySet()) {
				
			String movie = movies.get(movieID);
						
			// loop over each actor in that movie as long as it has actors
			if (movieActors.get(movie) != null) {
				for (String actor : movieActors.get(movie)) {
						
					// loop over each costar in that movie
					for (String costar : movieActors.get(movie)) {
							
						// as long as the actor is not the costar
						if (actor != costar) {
								
							// if edge hasn't yet been made, add an edge
							if (!originalGraph.hasEdge(actor, costar)) originalGraph.insertUndirected(actor, costar, new HashSet<String>());
								
							// add shared movie to the set (edge weight)
							originalGraph.getLabel(actor, costar).add(movie);
						}
					}
				}
			}
		}
	}
		
	/**
	 * helper method to create a map of actors and their IDs
	 * this method holds a try-catch block to catch an IOException
	 */
	private void createActors() {
		try {
			// create BufferedReader object to read through actors file
			BufferedReader input = new BufferedReader(new FileReader("inputs/actors.txt"));
			
			// instantiate string for each line of the file
			String line = input.readLine();
			
			// loop through each line of the file
			while(line != null) {
					
				// create a list separating out the ID and the actor name
				String[] list = line.split("\\|");
					
				// add ID and actor name to map
				actors.put(list[0], list[1]);
					
				// increment loop
				line = input.readLine();
			}
				
			input.close();
		}
		catch (IOException e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
		
	/**
	 * helper method to create a map of actors and their IDs
	 * this method holds a try-catch block to catch an IOException
	 */
	private void createMovies() {
		try {
			// create BufferedReader object to read through actors file
			BufferedReader input = new BufferedReader(new FileReader("inputs/movies.txt"));
				
			// instantiate string for each line of the file
			String line = input.readLine();
				
			// loop through each line of the file
			while(line != null) {
					
				// create a list separating out the ID and the actor name
				String[] list = line.split("\\|");
					
				// add ID and actor name to map
				movies.put(list[0], list[1]);
					
				// increment loop
				line = input.readLine();
			}
				
			input.close();
		}
		catch (IOException e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
		
		
	/**
	 * helper method to create a map of actors and their IDs
	 * this method holds a try-catch block to catch an IOException
	 */
	private void createMovieActors() {
		try {
			// create BufferedReader object to read through actors file
			BufferedReader input = new BufferedReader(new FileReader("inputs/movie-actors.txt"));
				
			// instantiate string for each line of the file
			String line = input.readLine();
				
			// loop through each line of the file
			while(line != null) {
					
				// create a list separating out the ID and the actor name
				String[] list = line.split("\\|");
					
				// create strings for the movie and the actor
				String movie = movies.get(list[0]);
				String actor = actors.get(list[1]);
					
				// if the movie has not yet been added to the map of movies to actors, add it (create new set as value)
				if (!movieActors.containsKey(movie)) movieActors.put(movie, new HashSet<String>());
					
				// add the actor to the movie in the map of movies to actors
				movieActors.get(movie).add(actor);

				// increment loop
				line = input.readLine();
			}
				
			input.close();
		}
		catch (IOException e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
	
	// EVENT-RELATED METHODS LISTED BELOW
	
	/**
	 * update the center of universe graph with a new center of the universe
	 * @param name of the center of the universe
	 */
	private void newCenterOfUniverse(String name) {
		// set the center of universe graph to a new graph returned from the GraphLibrary method bfs
		COIGraph = GraphLibrary.bfs(originalGraph, name);
		
		// print out the new center of the universe
		int numActors = numberOfPaths();
		double avgSeparation = GraphLibrary.averageSeparation(COIGraph, name);
		
		System.out.println(name + " is now the center of the acting universe, connected to " + numActors + "/" + actors.size() + " actors with average separation " + avgSeparation + ".");
	}
	
	/**
	 * get path from current center of the universe to an actor
	 * @param g			graph
	 * @param name		name of actor
	 * @return
	 */
	private List<String> getPathToActor(Graph<String, Set<String>> g, String name) {
		return GraphLibrary.getPath(g, name);
	}
	
	/**
	 * get number of vertices in the center of universe graph that have a path to the center of the universe
	 * @return
	 */
	private int numberOfPaths() {
		return originalGraph.numVertices() - GraphLibrary.missingVertices(originalGraph, COIGraph).size();
	}
	
	/**
	 * finds the average path length over all actors who are connected by some path to the current center
	 * @return	double average path length
	 */
	private double getAvgPathLength() {
		return GraphLibrary.averageSeparation(COIGraph, centerOfUniverse);
	}
	
	/**
	 * returns the degree of that actor - number of steps from center of universe
	 * @param actor		name of actor
	 * @return			their degree
	 */
	private int getDegree(String actor) {
		List<String> path = GraphLibrary.getPath(COIGraph, actor);
		return path.size() - 1;
	}

	
	// USER INPUT METHODS LISTED BELOW
	
	/**
	 * create and run the scanner until the user quits the program - by pressing key 'q'
	 */
	public void startScanner() {
		// construct a new scanner object
		Scanner in = new Scanner(System.in);
		
		// create a boolean identifying whether or not to keep scanning input
		boolean next = in.hasNext();
		
		// loop over that boolean
		while(next) {
			// grab the next line, its identifier, and its message
			String line = in.nextLine();
			char identifier = line.charAt(0);
			
			// if the user wants to exit the program, quit loop and identify user program has ended
			if (identifier == 'q') {
				next = false;
				System.out.println("Program terminated.");
			}
			
			// otherwise, pass the input on to the input handler method
			else {				
				// surrounded in try-catch because input handler throws exception if received unexpected input
				try {
					handleInput(identifier, line);
				}
				catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}
		
	/**
	 * handler method for all inputs given by the user
	 * @param id				the first character typed by the user - regarded as the identifier for what method to call
	 * @param s				the entire message typed by the user
	 * @throws Exception		if given unidentified id, throw exception for unexpected input
	 */
	public void handleInput(char id, String s) throws Exception {
		
		// list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
		if (id == 'c') {
			// get the message
			s = s.substring(2, s.length());			
			int num = Integer.parseInt(s);
			
			// boolean based on if given number is a top or bottom input
			boolean top;
			
			// create a graph and a priority queue
			Graph<String, Set<String>> testGraph = new AdjacencyMapGraph<String, Set<String>>();
			PriorityQueue<ActorDegree> pq;
				
			// if given a positive number, list center of universes sorted top to bottom
			if (num >= 0) {
				pq = new PriorityQueue<ActorDegree>(new ActorComparatorPos());
				top = true;
			}
			// if given a negative number, list center of universes sorted bottom to top
			else {
				pq = new PriorityQueue<ActorDegree>(new ActorComparatorNeg());
				top = false;
			}
				
			System.out.println("Calculating...");
				
			// loop of all actors, figure out their average separation and add to priority queue
			for (String actorID : actors.keySet()) {
				String actor = actors.get(actorID);
					
				// create graph centered around that actor, calculate avg separation, add to priority queue
				testGraph = GraphLibrary.bfs(originalGraph, actor);
				double myAvg = GraphLibrary.averageSeparation(testGraph, actor);
					
				pq.add(new ActorDegree(actor, myAvg));
			}
				
			// create a list and add actors to it from priority queue
			List<String> list = new ArrayList<String>();
			while (!pq.isEmpty()) {
				list.add(pq.remove().getName());
			}
			
			if (top) System.out.println("List of best center of universes sorted top to bottom:");
			else System.out.println("List of worst center of universes sorted bottom to top:");

			// print the list
			System.out.println(list);
		}

		
		// <low> <high>: list actors sorted by degree, with degree between low and high
		else if (id == 'd') {
			// splice the received message to get a low and high degree
			s = s.substring(2, s.length());
			String[] slist = s.split("\\ ");
			
			int low = Integer.parseInt(slist[0]);
			int high = Integer.parseInt(slist[1]);
			
			// create a priority queue to store all the actors
			// the pq holds ActorDegree objects (which simply store Actor name and their degree)
			// the pq is passed an ActorComparator object (which defines how to compare Actors -- by their degree)
			PriorityQueue<ActorDegree> pq = new PriorityQueue<ActorDegree>(new ActorComparatorPos());
			
			// create a set of actors not in the center of universe graph - that way can make sure not to throw Exception by asking for null path
			Set<String> missingActors = GraphLibrary.missingVertices(originalGraph, COIGraph);
			
			// loop over all actors, add them to the pq as long as they have a path
			for (String actorID : actors.keySet()) {
				// convert ID to name
				String actor = actors.get(actorID);
				
				// as long as this actor has a valid path, and their degree falls in the inputted range, add them to the pq
				if (!missingActors.contains(actor)) {
					int degree = getDegree(actor);
					
					if (degree >= low && degree <= high) {
						pq.add(new ActorDegree(actor, degree));
					}
				}
			}
						
			// create a list and add actors to it from priority queue
			List<String> list = new ArrayList<String>();
			while (!pq.isEmpty()) {
				list.add(pq.remove().getName());
			}
			
			System.out.println("Actors in range (" + low + "," + high + ") sorted by degree:");
			System.out.println(list);
		}
		
		// list actors with infinite separation from the current center
		else if (id == 'i') {
			System.out.println("Actors with infinite separation from " + centerOfUniverse + ":");		
			System.out.println(GraphLibrary.missingVertices(originalGraph, COIGraph));
		}
		
		// find path from <name> to current center of the universe
		else if (id == 'p') {
			// get the actor's name and their degree
			String actor = s.substring(2, s.length());
			
			// create a set of all missing actors - those with infinite separation
			Set<String> missing = GraphLibrary.missingVertices(originalGraph, COIGraph);
			
			// if this actor is not connected, end check
			if (missing.contains(actor)) System.out.println(actor + " is not connected to " + centerOfUniverse + ".");
			
			// otherwise, create a path
			else {
				int degree = getDegree(actor);
				// print information about center of universe and this actor's degree
				System.out.println(centerOfUniverse + " game >");			
				System.out.println(actor + "'s number is " + (degree));
				
				// create a list to store the path of this actor
				List<String> path = GraphLibrary.getPath(COIGraph, actor);
					
				// loop over list indices, printing path to the center (and what movies they appeared in)
				for (int i=0; i<path.size() -1; i++) {
					System.out.println(path.get(i) + " appeared in " + COIGraph.getLabel(path.get(i), path.get(i+1)) + " with " + path.get(i+1));
				}
			}
		}
		
		// <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
		else if (id == 's') {
			// splice the message to get the bounds of separation (low and high)
			s = s.substring(2, s.length());
			String[] slist = s.split("\\ ");
			
			int low = Integer.parseInt(slist[0]);
			int high = Integer.parseInt(slist[1]);
			
			// create a priority queue to store all the actors
			// the pq holds ActorDegree objects (which simply store Actor name and their degree)
			// the pq is passed an ActorComparator object (which defines how to compare Actors -- by their degree)
			PriorityQueue<ActorDegree> pq = new PriorityQueue<ActorDegree>(new ActorComparatorPos());
			
			// create a set of actors not in the center of universe graph - that way can make sure not to throw Exception by asking for null path
			Set<String> missingActors = GraphLibrary.missingVertices(originalGraph, COIGraph);
			
			// loop over all the actors
			for (String actorID : actors.keySet()) {
				// grab their real name
				String actor = actors.get(actorID);
				
				// so long as they don't have an infinite separation from the current center, get their path length
				if (!missingActors.contains(actor)) {
					List<String> path = GraphLibrary.getPath(COIGraph, actor);
					int length = path.size() - 1;
					
					// if the length is within the bounds, add a ActorDegree object to the priority queue
					if (length >= low && length <= high) {
						pq.add(new ActorDegree(actor, length));
					}
				}
			}
			
			// create a list to store actors and add one by one from the priority queue
			ArrayList<String> returnActors = new ArrayList<String>();
			while (!pq.isEmpty()) {
				returnActors.add(pq.remove().getName());
			}
			
			// print the list
			System.out.println("Actors in range (" + low + "," + high + ") sorted by non-infinite separation from the current center:");
			System.out.println(returnActors);

		}

		// make the given name the center of the universe
		else if (id == 'u') {
			setCenterOfUniverse(s.substring(2, s.length()));
		}
		
		// find number of actors who have a path to current center of universe
		else if (id == 'n') {
			int num = COIGraph.numVertices();
			System.out.println("There are " + num + " actors who have a path to " + centerOfUniverse);
		}
		
		// find average path length of all actors connected to current center of universe
		else if (id== 'a') {
			double avgSeparation = GraphLibrary.averageSeparation(COIGraph, centerOfUniverse);
			System.out.println("Average path length of all actors connected to " + centerOfUniverse + ": " + avgSeparation);
		}

		// if cannot handle the input (i.e. given unrecognized character) throw an exception
		else throw new Exception("Unexpected input");
	}
	
	/**
	 * main method
	 * creates a bacon object and runs the scanner
	 */
	public static void main(String[] args) {
		Bacon bacon = new Bacon();
		bacon.startScanner();
	}	
}