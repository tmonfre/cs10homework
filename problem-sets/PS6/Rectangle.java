import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class Rectangle implements Shape {
	private int x1, y1, x2, y2;
	private Color color;
	
	/**
	 * An "empty" rectangle, with only one point set so far
	 */
	public Rectangle(int x1, int y1, Color c) {
		// set given values for top left
		this.x1 = x1;
		this.y1 = y1;
		
		// set not given values to same
		this.x2 = x1;
		this.y2 = y1;
		
		// update color
		color = c;
	}
	
	/**
	 * A rectangle defined by two corners
	 */
	public Rectangle(int x1, int y1, int x2, int y2, Color c) {
		setCorners(x1,y1,x2,y2);
		this.color = c;
	}
	
	/**
	 * Redefines the rectangle based on new corners
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		// Ensure correct upper left and lower right
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
	}
	

	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx;
		x2 += dx;
		y1 += dy;
		y2 += dy;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
		
	/**
	 * this code is taken from the Ellipse class (credit to Chris Bailey-Kellogg)
	 */
	@Override
	public boolean contains(int x, int y) {
		double a = (x2-x1)/2.0, b = (y2-y1)/2.0;
		double dx = x - (x1 + a); // horizontal distance from center
		double dy = y - (y1 + b); // vertical distance from center

		// Apply the standard geometry formula. (See CRC, 29th edition, p. 178.)
		return Math.pow(dx / a, 2) + Math.pow(dy / b, 2) <= 1;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2-x1, y2-y1);
	}

	public String toString() {
		return "rectangle " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + color.getRGB();
	}
}
