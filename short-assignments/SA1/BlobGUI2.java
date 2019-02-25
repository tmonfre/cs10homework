import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 * Animated blob.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016, based on animated agents from previous terms
 * @author Thomas Monfre, Winter 2018, modified to allow presence of multiple blobs, not just one
 */
public class BlobGUI2 extends DrawingGUI {
	private static final int width=800, height=600;		// setup: size of the "world" (window)

	private ArrayList<Blob> blobs;					// the blobs
	private char blobType = '0';						// what type of blob to create
	private int delay = 100;							// delay for the timer to fire (and repeat) in milliseconds
	
	public BlobGUI2() {
		super("Animated Blob", width, height); //set up graphics "world"
	
		// Create blob.
		blobs = new ArrayList<Blob>();;  // What happens if this isn't here? Try it so you see the symptom.
		blobs.add(new Blob(width/2, height/2));
		
		// Timer drives the animation.
		setTimerDelay(delay);
		startTimer();
	}
	
	/**
	 * DrawingGUI method, here either detecting if the blob was clicked,
	 * or creating a new blob.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		
		for (Blob blob : blobs) {
			if (blob.contains(x, y)) {
				System.out.println("back off!");
				return;
			}
		}
		if (blobType=='0') {
			blobs.add(new Blob(x,y));
		}
		else if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='p') {
			blobs.add(new Pulsar(x,y));
		}
		else if (blobType=='t') {
			blobs.add(new Teleporter(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else if (blobType=='u') {
			blobs.add(new WanderingPulsar(x,y));
		}
		else if (blobType=='n') {
			blobs.add(new PurposefulWanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
		
		// Redraw with new blob
		repaint();
	}

	/**
	 * DrawingGUI method, here just remembering the type of blob to create
	 */
	@Override
	public void handleKeyPress(char k) {
		System.out.println("Handling key '"+k+"'");
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
		else { // blob type
			System.out.println("blob type:"+k);
			blobType = k;			
		}
	}
	
	/**
	 * DrawingGUI method, here just drawing the blob
	 */
	@Override
	public void draw(Graphics g) {
		// Ask the blob to draw itself.
		for (Blob blob: blobs) {
			blob.draw(g);
		}
	}
	
	/**
	 * DrawingGUI method, here having the blob take a step
	 */
	@Override
	public void handleTimer() {
		// Ask the blob to move itself.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Now update the GUI.
		repaint(); //repaint calls draw() in addition to a lot of other things
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BlobGUI2();
			}
		});
	}
}
