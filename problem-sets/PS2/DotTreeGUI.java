import java.awt.*;
import java.util.List;

import javax.swing.*;

/**
 * Driver for interacting with a quadtree:
 * inserting points, viewing the tree, and finding points near a mouse press
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for dots
 * @author CBK, Fall 2016, generics, dots, extended testing
 * @author Thomas Monfre, Winter 2018, completed DrawingGUI methods and added extra testing
 */
public class DotTreeGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe
	private static final int dotRadius = 5;				// to draw dot, so it's visible
	private static final Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
			// to color different levels differently

	private PointQuadtree<Dot> tree = null;			// holds the dots
	private char mode = 'a';						// 'a': adding; 'q': querying with the mouse
	private int mouseX, mouseY;						// current mouse location, when querying
	private int mouseRadius = 10;					// circle around mouse location, for querying
	private boolean trackMouse = false;				// if true, then print out where the mouse is as it moves
	private List<Dot> found = null;					// who was found near mouse, when querying
	
	public DotTreeGUI() {
		super("dottree", width, height);
	}

	/**
	 * DrawingGUI method, here keeping track of the location and redrawing to show it
	 */
	@Override
	public void handleMouseMotion(int x, int y) {
		// set mouse location for querying 
		if (mode == 'q') {
			mouseX = x; mouseY = y;
			repaint();
		}
		// print mouse location if trackMouse enabled
		if (trackMouse) {
			System.out.println(x+","+y);
		}
	}

	/**
	 * DrawingGUI method, here either adding a new point or querying near the mouse
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (mode == 'a') {			
			// Add a new dot at the point
			if (tree==null) tree = new PointQuadtree<Dot>(new Dot(x,y), 0, 0, width-1, height-1);
			else tree.insert(new Dot(x,y));
		}
		else if (mode == 'q') {
			// Set "found" to what tree says is near the mouse press
			found = tree.findInCircle(x,y,mouseRadius);
		}
		else {
			System.out.println("clicked at "+x+","+y);
		}
		repaint();
	}
	
	/**
	 * A simple testing procedure, making sure actual is expected, and printing a message if not
	 * @param x		query x coordinate
	 * @param y		query y coordinate
	 * @param r		query circle radius
	 * @param expectedCircleRectangle	how many times Geometry.circleIntersectsRectangle is expected to be called
	 * @param expectedInCircle			how many times Geometry.pointInCircle is expected to be called
	 * @param expectedHits				how many points are expected to be found
	 * @return  0 if passed; 1 if failed
	 */
	private int testFind(int x, int y, int r, int expectedCircleRectangle, int expectedInCircle, int expectedHits) {
		Geometry.resetNumInCircleTests();
		Geometry.resetNumCircleRectangleTests();
		int errs = 0;
		int num = tree.findInCircle(x, y, r).size();
		String which = "("+x+","+y+")@"+r;
		if (Geometry.getNumCircleRectangleTests() != expectedCircleRectangle) {
			errs++;
			System.err.println(which+": wrong # circle-rectangle, got "+Geometry.getNumCircleRectangleTests()+" but expected "+expectedCircleRectangle);
		}
		if (Geometry.getNumInCircleTests() != expectedInCircle) {
			errs++;
			System.err.println(which+": wrong # in circle, got "+Geometry.getNumInCircleTests()+" but expected "+expectedInCircle);
		}
		if (num != expectedHits) {
			errs++;
			System.err.println(which+": wrong # hits, got "+num+" but expected "+expectedHits);
		}
		return errs;
	}
	
	/**
	 * test tree 0 -- first three points from figure in handout
	 * hardcoded point locations for 800x600
	 */
	private void test0() {
		found = null;
		tree = new PointQuadtree<Dot>(new Dot(400,300), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450)); // B
		tree.insert(new Dot(250,550)); // C
		int bad = 0;
		bad += testFind(0,0,900,3,3,3);		// rect for all; circle for all; find all
		bad += testFind(400,300,10,3,2,1);	// rect for all; circle for A,B; find A
		bad += testFind(150,450,10,3,3,1);	// rect for all; circle for all; find B
		bad += testFind(250,550,10,3,3,1);	// rect for all; circle for all; find C
		bad += testFind(150,450,200,3,3,2);	// rect for all; circle for all; find B, C
		bad += testFind(140,440,10,3,2,0);	// rect for all; circle for A,B; find none
		bad += testFind(750,550,10,2,1,0);	// rect for A,B; circle for A; find none
		if (bad==0) System.out.println("test 0 passed!");
	}

	/**
	 * test tree 1 -- figure in handout
	 * hardcoded point locations for 800x600
	 */
	private void test1() {
		found = null;
		tree = new PointQuadtree<Dot>(new Dot(300,400), 0,0,800,600); // start with A
		tree.insert(new Dot(150,450)); // B
		tree.insert(new Dot(250,550)); // C
		tree.insert(new Dot(450,200)); // D
		tree.insert(new Dot(200,250)); // E
		tree.insert(new Dot(350,175)); // F
		tree.insert(new Dot(500,125)); // G
		tree.insert(new Dot(475,250)); // H
		tree.insert(new Dot(525,225)); // I
		tree.insert(new Dot(490,215)); // J
		tree.insert(new Dot(700,550)); // K
		tree.insert(new Dot(310,410)); // L
		int bad = 0;
		bad += testFind(150,450,10,6,3,1); 	// rect for A [D] [E] [B [C]] [K]; circle for A, B, C; find B
		bad += testFind(500,125,10,8,3,1);	// rect for A [D [G F H]] [E] [B] [K]; circle for A, D, G; find G
		bad += testFind(300,400,15,10,6,2);	// rect for A [D [G F H]] [E] [B [C]] [K [L]]; circle for A,D,E,B,K,L; find A,L
		bad += testFind(495,225,50,10,6,3);	// rect for A [D [G F H [I [J]]]] [E] [B] [K]; circle for A,D,G,H,I,J; find H,I,J
		bad += testFind(0,0,900,12,12,12);	// rect for all; circle for all; find all
		if (bad==0) System.out.println("test 1 passed!");
	}
	
	/**
	 * test tree 2 - set 5 points all in respective fourth quadrant, then check hits around
	 * hardcoded point locations for 800x600
	 */
	private void test2() {
		found = null;
		tree = new PointQuadtree<Dot>(new Dot(137,128), 0,0,800,600); // start with A
		tree.insert(new Dot(240,210)); // B
		tree.insert(new Dot(330,282)); // C
		tree.insert(new Dot(418,351)); // D
		tree.insert(new Dot(487,407)); // E
		
		int bad = 0;
		bad += testFind(0,0,900,5,5,5);		// rect for all; circle for all; find all
		bad += testFind(498,419,10,5,5,0);	// rect for all; circle for all; find none (last quadrant)
		bad += testFind(483,404,10,5,5,1);	// rect for all; circle for all; find E
		bad += testFind(428,361,10,5,5,0);	// rect for all; circle for all; find none
		bad += testFind(414,349,200,5,5,3);	// rect for all; circle for all; find B, C, D
		bad += testFind(341,291,10,5,4,0);	// rect for all; circle for all but E; find none
		bad += testFind(245,211, 10,4,3,1);  // rect for all but E; circle for all but D,E; find A
		if (bad==0) System.out.println("test 2 passed!");
	}

	/**
	 * DrawingGUI method, here toggling the mode between 'a' and 'q'
	 * and increasing/decresing mouseRadius via +/-
	 */
	@Override
	public void handleKeyPress(char key) {
		if (key=='a' || key=='q') mode = key;
		else if (key=='+') { // increase mouse radius
			mouseRadius += 10;
		}
		else if (key=='-') { // decrease mouse radius
			mouseRadius -= 10;
			if (mouseRadius < 0) mouseRadius=0;
		}
		else if (key=='c') {   // clear the tree and place a dot at the middle of the screen
			tree = new PointQuadtree<Dot>(new Dot(width/2, height/2), 0, 0, width-1, height-1);
		}
		else if (key=='m') {  // track mouse
			trackMouse = !trackMouse;
		}
		else if (key=='0') {  // run test0
			test0();
		}
		else if (key=='1') {  // run test1
			test1();
		}
		else if (key=='2') {  // run test2
			test2();
		}
		repaint();
	}
	
	/**
	 * DrawingGUI method, here drawing the quadtree
	 * and if in query mode, the mouse location and any found dots
	 */
	@Override
	public void draw(Graphics g) {
		if (tree != null) drawTree(g, tree, 0);
		if (mode == 'q') {
			g.setColor(Color.BLACK);
			g.drawOval(mouseX-mouseRadius, mouseY-mouseRadius, 2*mouseRadius, 2*mouseRadius);			
			if (found != null) {
				g.setColor(Color.BLACK);
				for (Dot d : found) {
					g.fillOval((int)d.getX(), (int)d.getY(), 2*dotRadius, 2*dotRadius);
				}
			}
		}
	}

	/**
	 * Draws the dot tree
	 * @param g		the graphics object for drawing
	 * @param tree	a dot tree (not necessarily root)
	 * @param level	how far down from the root qt is (0 for root, 1 for its children, etc.)
	 */
	public void drawTree(Graphics g, PointQuadtree<Dot> tree, int level) {
		// Set the color for this level
		g.setColor(rainbow[level % rainbow.length]);
		
		// Draw this node's dot and lines through it
		g.fillOval((int)tree.getPoint().getX(), (int)tree.getPoint().getY(), dotRadius*2, dotRadius*2); // dot
		g.drawLine((int)tree.getPoint().getX()+dotRadius, tree.getY1()+dotRadius, (int)tree.getPoint().getX()+dotRadius, tree.getY2()+dotRadius); // vertical line
		g.drawLine(tree.getX1()+dotRadius, (int)tree.getPoint().getY()+dotRadius, tree.getX2()+dotRadius, (int)tree.getPoint().getY()+dotRadius); // horizontal line
		
		// Recurse with children
		if (tree.hasChild(1)) drawTree(g, tree.getChild(1), level+1);
		if (tree.hasChild(2)) drawTree(g, tree.getChild(2), level+1);
		if (tree.hasChild(3)) drawTree(g, tree.getChild(3), level+1);
		if (tree.hasChild(4)) drawTree(g, tree.getChild(4), level+1);
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DotTreeGUI();
			}
		});
	}
}
