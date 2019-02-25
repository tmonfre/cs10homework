import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * A class demonstrating manipulation of image pixels.
 * Version 0: just the core definition
 * Load an image and display it
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Winter 2014, rewritten for BufferedImage
 * @author CBK, Spring 2015, refactored to separate GUI from operations
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018, rewritten to handleMouseMotion
 */
public class ImageProcessingGUI0 extends DrawingGUI {
	private ImageProcessor0 proc;		// handles the image processing
	protected boolean brush = false;		// determines if the brush is up or down (activated on key press)
	static int radius = 3;				// static variable for radius size

	/**
	 * Creates the GUI for the image processor, with the window scaled to the to-process image's size
	 */
	public ImageProcessingGUI0(ImageProcessor0 proc) {
		super("Image processing", proc.getImage().getWidth(), proc.getImage().getHeight());
		this.proc = proc;
	}

	/**
	 * DrawingGUI method, here showing the current image
	 */
	@Override
	public void draw(Graphics g) {
		g.drawImage(proc.getImage(), 0, 0, null);
		
	}

	/**
	 * DrawingGUI method, here dispatching on image processing operations
	 */
	@Override
	public void handleKeyPress(char op) {
		System.out.println("Handling key '"+op+"'");
		if (op=='s') { // save a snapshot
			saveImage(proc.getImage(), "pictures/snapshot.png", "png");
		}
		else if (op=='b') { // set the brush down - allowing emphasizeColor to perform
			brush = !brush;
		}
		else if (op=='+' && radius < 10) { // increase brush radius size
			radius += 1;
		}
		else if(op=='-' && radius > 1) { // decrease brush radius size
			radius -= 1;
		}
		else {
			System.out.println("Unknown operation");
		}

		repaint(); // Re-draw, since image has changed
	}
	
	/**
	 * if the brush is down (set to true), call emphasizeColor on the ImageProcessor
	 * then repaint the screen
	 * 
	 * @param x		x location
	 * @param y		y location
	 */
	@Override
	public void handleMouseMotion(int x, int y) {
		if(brush == true) {
			proc.emphasizeColor(x, y, radius);
			repaint();
		}
	}

	public static void main(String[] args) { 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Load the image to process
				BufferedImage baker = loadImage("pictures/baker.jpg");
				// Create a new processor, and a GUI to handle it
				new ImageProcessingGUI0(new ImageProcessor0(baker));
			}
		});
	}
}
