import java.io.PrintWriter;

/**
 * Static methods to send requests to a client/server
 * 
 * @author Thomas Monfre, Dartmouth College CS 10, Winter 2018
 */
public class SendRequests {
	
	/**
	 * request to draw a shape
	 * @param shape			shape object
	 * @param out			PrintWriter object of the thread
	 */
	public static void requestAddShape(Shape shape, PrintWriter out) {
		// syntax is keyword draw followed by the toString method of that object
		// this follows the construct of keyword for type of shape, its point values, and its color
		String s = "draw " + shape.toString();
		out.println(s);
	}
	
	/**
	 * request to move an object to a point
	 * @param id			object id
	 * @param x			desired x change
	 * @param y			desired y change
	 * @param out			PrintWriter object of the thread
	 */
	public static void requestMove(int id, int dx, int dy, PrintWriter out) {
		// syntax is keyword move, shape ID, dx, and dy
		String s = "move " + id + " " + dx + " " + dy;
		out.println(s);
	}
	
	/**
	 * request to recolor a shape
	 * @param id			object id
	 * @param out			PrintWriter object of the thread
	 */
	public static void requestRecolor(int id, int color, PrintWriter out) {
		// syntax is keyword recolor followed by shape ID and desired new color
		String s = "recolor " + id + " " + color;
		out.println(s);		
	}
	
	/**
	 * request to delete an object
	 * @param id			object id
	 * @param out			PrintWriter object of the thread
	 */
	public static void requestDelete(int id, PrintWriter out) {
		// syntax is keyword delete followed by ID of which shape to delete
		String s = "delete " + id;
		out.println(s);		
	}
}