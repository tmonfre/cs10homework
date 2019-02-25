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
 * 		   EC version also allows for painting over live webcam footage	
 */
public class CamPaintEC extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting or 'l': live webcam painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private ArrayList<Point> drawing;  // list of actively drawn points to overlay webcam
	private boolean pause = false;					// boolean to determine whether or not to pause the drawing
	private ArrayList<Color> brushColors;	// list of brush colors
	private int brushElement;				// indicating location of chosen color in list

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaintEC() {
		finder = new RegionFinder();
		clearPainting();
		
		// instantiate list of pixels for drawing over live webcam footage and list of paint brush colors
		drawing = new ArrayList<Point>();
		brushColors = new ArrayList<Color>();
		brushColors.add(Color.blue);
		brushColors.add(Color.green);
		brushColors.add(Color.red);
		
		// set paintColor to first element in brush list
		brushElement = 0;
		paintColor = brushColors.get(brushElement);
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		if (displayMode == 'p') painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		else if (displayMode == 'l') drawing.clear();
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// display webcam
		if (displayMode == 'w' || displayMode == 'l') {
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
			
			// check for targetColor and that the drawing is not paused
			if (targetColor != null && !pause) {
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
		
		// draw painting over webcam footage
		if (displayMode == 'l') {

			// check for targetColor and that the drawing is not paused
			if (targetColor != null && !pause) {
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
			
			
			// update the painting
			if (largestRegion != null) {
				for (Point pixel : largestRegion) {
					if (pixel != null) {
						drawing.add(pixel);
					}
				}
			}
			}
			
			for (Point pixel : drawing) {
				image.setRGB((int) pixel.getX(), (int) pixel.getY(), paintColor.getRGB());
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
		if (k == 'p' || k == 'r' || k == 'w' || k == 'l') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if(k == ' ') { // pause the painting
			pause = !pause;
		}
		else if(k == 'b') { // change brush color
			if (brushElement < brushColors.size() - 1) {
				paintColor = brushColors.get(brushElement + 1);
				brushElement++;
			}
			else {
				paintColor = brushColors.get(0);
				brushElement = 0;
			}
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
				new CamPaintEC();
			}
		});
	}
}
