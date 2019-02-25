import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class Polyline implements Shape {
	private ArrayList<Point> points;
	private Color color;
	
	public Polyline(Point p, Color c) {
		points = new ArrayList<Point>();
		points.add(p);
		color = c;
	}
	
	public void addPoint(Point p) {
		points.add(p);
	}
	
	@Override
	public void moveBy(int dx, int dy) {
		for (Point p : points) {
			p.setLocation(p.getX() + dx, p.getY() + dy);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean contains(int x, int y) {
		int index = 0;
		
		while (index < points.size() - 1) {
			Point left = points.get(index);
			Point right = points.get(index + 1);
			
			if (Segment.pointToSegmentDistance(x, y, (int)left.getX(), (int)left.getY(), (int)right.getX(), (int)right.getY()) <= 3) {
				return true;
			}
			
			index++;
		}
		
		return false;
	
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		int index = 0;
		
		while (index < points.size() - 1) {
			Point left = points.get(index);
			Point right = points.get(index + 1);
			g.drawLine((int)left.getX(), (int)left.getY(), (int)right.getX(), (int)right.getY());
			index++;
		}
		
	}

	@Override
	public String toString() {
		String s = "polyline ";
		
		for (Point p : points) {
			int x = (int)p.getX();
			int y = (int)p.getY();
			
			s += x + ";" + y + " ";
		}
		
		s += color.getRGB();
		
		return s;
	}
}
