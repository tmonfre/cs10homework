import java.awt.Color;
import java.util.*;

/**
 * Collection of shape objects for graphics window
 * 
 * @author Thomas Monfre, Dartmouth College CS 10, Winter 2018
 */
public class Sketch {
	// map of shape ID and shape objects
	private Map<Integer, Shape> map;
	
	// reference to last given ID
	private static int lastID;
	
	/**
	 * constructor method, instantiates map and sets lastID to 0
	 */
	public Sketch() {
		 map = new TreeMap<Integer, Shape>();
		 lastID = 0;
	}
	
	/**
	 * getter for the map
	 */
	public synchronized Map<Integer, Shape> getMap() {
		return map;
	}
	
	/**
	 * add's shape s to the map and increments lastID
	 * @param s			shape object (could be Ellipse, Rectangle, Polyline, Segment, etc. -- just has to implement Shape interface)
	 */
	public synchronized void addToMap(Shape s) {
		map.put(lastID++, s);
	}
	
	/**
	 * for each shape in the map, check if user clicked on it
	 * @param x			x location of click
	 * @param y			y location of click
	 * @return			shape id if found, -1 if not
	 */
	public synchronized Integer clickedOnShape(int x, int y) {
		// create reverse order key set
		ArrayList<Integer> reverseKeySet = new ArrayList<Integer>();
		for (Integer id : map.keySet()) {
			reverseKeySet.add(0, id);
		}
		
		// loop over the reverse order set ArrayList and return id if found
		for (Integer id : reverseKeySet) {
			if (map.get(id).contains(x, y)) {
				return id;
			}
		}
		
		return -1;
	}
	
}
