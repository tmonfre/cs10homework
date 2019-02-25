import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * @author Thomas Monfre, Winter 2018, filled in methods
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// create local variables for the new point's x and y location
		int newX = (int) p2.getX();
		int newY = (int) p2.getY();
		
		// create local variables for this point's x and y location
		int x = (int) point.getX();
		int y = (int) point.getY();
		
		// check if new point is in quadrant 1
		if (newX > x && newX < x2 && newY > y1 && newY < y) {
			if (hasChild(1)) c1.insert(p2);
			else c1 = new PointQuadtree<E>(p2,x,y1,x2,y);
		}
		
		// check if new point is in quadrant 2
		else if (newX > x1 && newX < x && newY > y1 && newY < y) {
			if (hasChild(2)) c2.insert(p2);
			else c2 = new PointQuadtree<E>(p2,x1,y1,x,y);
		}
		
		// check if new point is in quadrant 3
		else if (newX > x1 && newX < x && newY > y && newY < y2) {
			if (hasChild(3)) c3.insert(p2);
			else c3 = new PointQuadtree<E>(p2,x1,y,x,y2);
		}
		
		// check if new point is in quadrant 4
		else if (newX > x && newX < x2 && newY > y && newY < y2) {
			if (hasChild(4)) c4.insert(p2);
			else c4 = new PointQuadtree<E>(p2,x,y,x2,y2);
		}	
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int sum = 1;
		if (hasChild(1)) sum += c1.size();
		if (hasChild(2)) sum += c2.size();
		if (hasChild(3)) sum += c3.size();
		if (hasChild(4)) sum += c4.size();
		return sum;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 * Calls helper method allPointsHelper
	 */
	public List<E> allPoints() {
		return allPointsHelper(new ArrayList<E>());
	}
	
	/**
	 * helper method for allPoints method
	 * recurses on itself, passing the list along
	 * this maintains one reference to one list
	 */
	private List<E> allPointsHelper(List<E> l) {
		l.add(point);
		
		if (hasChild(1)) c1.allPointsHelper(l);
		if (hasChild(2)) c2.allPointsHelper(l);
		if (hasChild(3)) c3.allPointsHelper(l);
		if (hasChild(4)) c4.allPointsHelper(l);
		
		return l;
	}
	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 * 
	 * calls a helper method findInCircleHelper
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		return findInCircleHelper(new ArrayList<E>(), cx, cy, cr);
	}

	/**
	 * helper method for findInCircle
	 * used to determine all points in circle recursively
	 * passes a single reference to a list on each recursive call
	 * @param l		list to pass along
	 * @param cx		circle x
	 * @param cy		circle y
	 * @param cr		circle radius
	 * @return		the same reference to the list
	 */
	private List<E> findInCircleHelper(List<E> l, double cx, double cy, double cr) {
		Geometry geo = new Geometry();
		
		// if the circle intersects with the rectangle covering that region (x1,y1),(x2,y2)
		if (geo.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			// if the tree's point is in the circle, add it to the list
			if (geo.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
				l.add(point);
			}
			
			// recursively call the helper method on each quadrant
			if (hasChild(1)) c1.findInCircleHelper(l, cx, cy, cr);
			if (hasChild(2)) c2.findInCircleHelper(l, cx, cy, cr);
			if (hasChild(3)) c3.findInCircleHelper(l, cx, cy, cr);
			if (hasChild(4)) c4.findInCircleHelper(l, cx, cy, cr);
		}
	
		return l;
	}
	
}
