import java.awt.Color;
import java.awt.Point;
import java.util.Map;

/**
 * Static methods to handle client/server requests
 * 
 * @author Thomas Monfre, Dartmouth College CS 10, Winter 2018
 */
public class HandleRequests {
	
	/**
	 * responds to request from server to draw an object
	 * @param tokens			string of commands (shapeType followed by information about points, followed by color)
	 * @param sketch			sketch object
	 */
	public static void drawHelper(String[] tokens, Sketch sketch) {
		// get the type of the shape (i.e. "ellipse," "rectangle," etc.)
		String shapeType = tokens[1];
					
		// if ellipse, add ellipse object to editor's sketch map
		if (shapeType.equals("ellipse")) {
			// get values for ellipse
			int x1 = Integer.parseInt(tokens[2]);
			int y1 = Integer.parseInt(tokens[3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens[5]);
			int color = Integer.parseInt(tokens[6]);
			
			// add ellipse to editor's sketch
			sketch.addToMap(new Ellipse(x1, y1, x2, y2, new Color(color)));
		}
		
		// if rectangle, add rectangle object to editor's sketch map
		else if (shapeType.equals("rectangle")) {
			
			// get values for rectangle
			int x1 = Integer.parseInt(tokens[2].substring(0,tokens[2].length()-1));
			int y1 = Integer.parseInt(tokens[3].substring(0,tokens[3].length()-1));
			int x2 = Integer.parseInt(tokens[4].substring(0,tokens[4].length()-1));
			int y2 = Integer.parseInt(tokens[5].substring(0,tokens[5].length()-1));
			int color = Integer.parseInt(tokens[6]);
			
			// add rectangle to editor's sketch map
			sketch.addToMap(new Rectangle(x1, y1, x2, y2, new Color(color)));
		}
		
		// if polyline, add polyline object to editor's sketch map
		else if (shapeType.equals("polyline")) {
			// grab starting information
			String[] startPoint = tokens[2].split("\\;");
			Color color = new Color(Integer.parseInt(tokens[tokens.length - 1]));
			
			// construct polyline object
			Polyline p = new Polyline(new Point(Integer.parseInt(startPoint[0]), Integer.parseInt(startPoint[1])), color);
			
			// the string comes with all points that make up the polyline (in format x1;y1 x2;y2 x3;y3, etc.)
			// for all unadded points, add them to p's collection of points in the curve
			for (int i=4; i<tokens.length-2; i++) {
				// split to get x and y values for the point based on semi-colon
				String[] sPoint = tokens[i].split("\\;");
				
				p.addPoint(new Point(Integer.parseInt(sPoint[0]), Integer.parseInt(sPoint[1])));
				
			}
			
			// add polyline to editor's sketch map
			sketch.addToMap(p);
		}
		
		// if segment, add segment object to editor's sketch map
		else if (shapeType.equals("segment")) {
			// get values for segment
			int x1 = Integer.parseInt(tokens[2]);
			int y1 = Integer.parseInt(tokens[3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens[5]);
			int color = Integer.parseInt(tokens[6]);
			
			// add segment to editor's sketch then repaint
			sketch.addToMap(new Segment(x1, y1, x2, y2, new Color(color)));
		}
	}
	
	/**
	 * responds to request from server to move an object
	 * @param tokens			string of commands (shape ID, dx, dy)
	 * @param sketch			sketch object
	 */
	public static void moveHelper(String[] tokens, Sketch sketch) {
		// grab shape ID and values for how far to move
		int id = Integer.parseInt(tokens[1]);
		int dx = Integer.parseInt(tokens[2]);
		int dy = Integer.parseInt(tokens[3]);
		
		// grab reference to the shape from the editor's sketch (based on given ID)
		Shape shape = (Shape) sketch.getMap().get(id);
		
		// call that shape's move method
		shape.moveBy(dx, dy);
		
	}
	
	/**
	 * responds to request from server to move an object
	 * @param tokens			string of commands (shape ID, new color)
	 * @param sketch			sketch object
	 */
	public static void recolorHelper(String[] tokens, Sketch sketch) {	
		// grab shape ID and new color RGB value
		int id = Integer.parseInt(tokens[1]);
		int color = Integer.parseInt(tokens[2]);
		
		// grab reference to the shape from the editor's sketch (based on given ID)
		Shape shape = sketch.getMap().get(id);
		
		// update that shape's color
		shape.setColor(new Color(color));
	}
	
	/**
	 * responds to request from server to delete an object
	 * @param tokens			string of commands (shape ID)
	 * @param sketch			sketch object
	 */
	public static void deleteHelper(String[] tokens, Sketch sketch) {	
		// grab shape ID
		int id = Integer.parseInt(tokens[1]);
		
		// get reference to the editor's sketch map (map of shapes and their values) then delete this ID's shape from the map
		Map<Integer,Shape> map = sketch.getMap();
		map.remove(id);
	}
}