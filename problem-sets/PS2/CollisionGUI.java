import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 * @author Thomas Monfre, Winter 2018, completed DrawingGUI methods and added test method
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control
	private boolean test = false;					// used to determine if this is a test run
	private static int testBlobs = 10;				// number of blobs to use for test method

	public CollisionGUI() {
		super("super-collider", width, height);

		// instantiate lists
		blobs = new ArrayList<Blob>();
		colliders = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k=='0') { // run test method
			test0();
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.		
		for (Blob b : blobs) {
			b.draw(g);
		}
		
		// Ask the colliders to draw themselves in red.
		for (Blob c : colliders) {
			g.setColor(Color.red);
			c.draw(g);
		}
		
		// If running the test and passed, print to console, end test
		if (test && colliders.size() >= testBlobs) {
			System.out.println("test0 passed!");
			test = false;
		}
		
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Create the tree
		PointQuadtree<Blob> tree = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
		
		// insert all blobs to tree 
		for(int i=1; i<blobs.size(); i++) {
			tree.insert(blobs.get(i));
		}
		
		// For each blob, see if anybody else collided with it
		for (Blob b : blobs) {
			// list of blobs nearby that point
			List<Blob> nearby = tree.findInCircle((int) b.getX(), (int) b.getY(), (int) b.getR()*2);
			
			// remove blob b from the list of nearby points
			for (int i=0; i<nearby.size(); i++) {
				if (nearby.get(i) == b) {
					nearby.remove(i);
				}
			}
			
			// add nearby blobs to list of colliders if they aren't already there
			for (Blob n : nearby) {
				if (!colliders.contains(n)) {
					colliders.add(n);
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders.clear();
			}
		}
		
		// Now update the drawing
		repaint();
	}
	
	
	/**
	 * test that ensures blobs are correctly colliding
	 * creates sets of blobs that should, mathematically speaking, all collide
	 */
	public void test0() {
		// set the collision handler to color, not delete
		collisionHandler = 'c';
		
		// clear all blobs and colliders off the screen
		blobs.clear();
		colliders.clear();
		
		// set the boolean for active test as true
		test = true;
		
		// create five sets of blobs placed at random uniform locations across the screen
		for(int i=0; i<testBlobs/2; i++) {
			int b1Location = (int) (Math.random() * height/2);
			int b2Location = height/2 + (int) (Math.random() * height/2);
			Blob b1 = new Blob(b1Location, b1Location); // x and y are set to the same value
			Blob b2 = new Blob(b2Location, b2Location);
			
			// have the first blob move only to the right on the x axis
			b1.setVelocity(5,0);
			// have the second blob move only upwards on the y axis
			b2.setVelocity(0,-5);
			
			// add to the list of blobs
			blobs.add(b1);
			blobs.add(b2);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
