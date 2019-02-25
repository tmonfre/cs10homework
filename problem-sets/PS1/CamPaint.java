import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author Thomas Monfre, Winter 2018, implemented methods to recolor image and track regions using webcam
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// display webcam
		if (displayMode == 'w') {
			g.drawImage(image, 0, 0, null);
		}
		
		// display painting
		if (displayMode == 'p') {
			g.drawImage(painting,  0,  0,  null);
		}
		
		// display recolored image
		if (displayMode == 'r') {	
			// set image to the recoloredImage if a targetColor is set, else just show the image
			if(targetColor != null) image = finder.getRecoloredImage();
			else image = finder.getImage();

			// display the set image
			g.drawImage(image, 0, 0, null);
		}
		
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// draw the painting
		if (displayMode == 'p') {
			
			// check for targetColor
			if (targetColor != null) {
				// create new RegionFinder object and find all regions
				finder = new RegionFinder(image);
				finder.findRegions(targetColor);
				
				// determine the largest region, clear the list of regions, add only the largest region
				ArrayList<Point> largestRegion = finder.largestRegion();
				finder.regions.clear();
				finder.regions.add(largestRegion);
				
				// recolor the image and set the BufferedImage object image to the recolored image
				finder.recolorImage(paintColor);
				image = finder.getRecoloredImage();
				
				// initialize painting if not already done
				if (painting == null) {
					painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				}
				
				// update the painting
				if (largestRegion != null) {
					for (Point pixel : largestRegion) {
						if (pixel != null) {
							painting.setRGB((int) pixel.getX(), (int) pixel.getY(), paintColor.getRGB());

						}
					}
				}	
			}
		}
		
		// draw the recoloredImage of webcam
		if (displayMode == 'r') {
			// create new RegionFinder object
			finder = new RegionFinder(image);
			
			// check for targetColor
			if (targetColor != null) {
				// find regions and recolor the image
				finder.findRegions(targetColor);
				finder.recolorImage(paintColor);
				image = finder.getRecoloredImage();
			}	
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		targetColor = new Color(image.getRGB(x, y));
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
