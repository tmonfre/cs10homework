import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 35;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored
	
	public ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

		
	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// create a blank image ready to be recolored to indicate identified regions
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		// loop over all the pixels
		for (int y = 0; y < image.getHeight() - 1; y++) {
			for (int x = 0; x < image.getWidth() - 1; x++) {
				
				// check to see if this pixel is unvisited and its color is similar enough to the targetColor
				if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x,y)), targetColor)) {
					
					// create new region of pixels that match targetColor
					ArrayList<Point> newRegion = new ArrayList<Point>();
					
					// create queue of pixels left to be visited
					Queue<Point> toVisit = new Queue();
					toVisit.enqueue(new Point(x,y));
					
					while (toVisit.getLength() > 0) {						
						// get a pixel from the queue left to be visited
						Point point = toVisit.dequeue();
						
						// add it to the region
						newRegion.add(point);
						
						// mark it as visited (color that pixel white on the separate image)
						visited.setRGB((int)point.getX(), (int)point.getY(), 1);
						
						// create a list for this point's neighbors and add neighbors
						ArrayList<Point> neighbors = new ArrayList<Point>();
						
						// neighbors directly north, east, south, and west
						if (point.getY() > 0) {
							neighbors.add(new Point( (int)point.getX(), (int)point.getY() - 1)); // neighbor above
						}
						if (point.getY() < image.getHeight() - 1) {
							neighbors.add(new Point( (int)point.getX(), (int)point.getY() + 1)); // neighbor below
						}
						if (point.getX() > 0) {
							neighbors.add(new Point( (int)point.getX() - 1, (int)point.getY())); // neighbor to left
						}
						if (point.getX() < image.getWidth() - 1) {
							neighbors.add(new Point( (int)point.getX() + 1, (int)point.getY())); // neighbor to right
						}
						
						// neighbors directly northeast, northwest, southeast, and southwest
						if (point.getX() > 0 && point.getY() > 0) {
							neighbors.add(new Point( (int)point.getX() - 1, (int)point.getY() - 1)); // neighbor top left
						}
						if (point.getX() < image.getWidth() - 1 && point.getY() > 0) {
							neighbors.add(new Point( (int)point.getX() + 1, (int)point.getY() - 1)); // neighbor top right
						}
						if (point.getX() > 0 && point.getY() < image.getHeight() - 1) {
							neighbors.add(new Point( (int)point.getX() - 1, (int)point.getY() + 1)); // neighbor bottom left
						}
						if (point.getX() < image.getWidth() - 1 && point.getY() < image.getHeight() - 1) {
							neighbors.add(new Point( (int)point.getX() + 1, (int)point.getY() + 1)); // neighbor bottom right
						}

						// neighbors further north, east, south, and west
						if (point.getY() > 1) {
							neighbors.add(new Point( (int)point.getX(), (int)point.getY() - 2)); // neighbor above
						}
						if (point.getY() < image.getHeight() - 2) {
							neighbors.add(new Point( (int)point.getX(), (int)point.getY() + 2)); // neighbor below
						}
						if (point.getX() > 1) {
							neighbors.add(new Point( (int)point.getX() - 2, (int)point.getY())); // neighbor to left
						}
						if (point.getX() < image.getWidth() - 2) {
							neighbors.add(new Point( (int)point.getX() + 2, (int)point.getY())); // neighbor to right
						}
										
						// for each neighbor, if its color matches targetColor, add it to the queue
						for (Point neighbor : neighbors) {
							if ( colorMatch(new Color(image.getRGB((int) neighbor.getX(), (int) neighbor.getY())), targetColor) && visited.getRGB((int) neighbor.getX(), (int) neighbor.getY()) == 0 ) {
								toVisit.enqueue(neighbor);
								visited.setRGB((int)neighbor.getX(), (int)neighbor.getY(), 1);

							}
						}	
					}
					
					// if the region generated from this pixel is large enough to deem worthwhile, add it to the ArrayList regions
					if (newRegion.size() > minRegion) {
						regions.add(newRegion);
					}
					
				}
			}
		}
	}
	
	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		int redDiff, greenDiff, blueDiff, diff;
		
		redDiff = Math.abs(c1.getRed() - c2.getRed());
		greenDiff = Math.abs(c1.getGreen() - c2.getGreen());
		blueDiff = Math.abs(c1.getBlue() - c2.getBlue());
		
		diff = redDiff + greenDiff + blueDiff;
		
		if (diff <= maxColorDiff) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if (regions.size() != 0) {
			int max = 0;
			ArrayList<Point> largestRegion = regions.get(0);
			
			for (ArrayList<Point> region : regions) {
				if (region.size() >= max) {
					max = region.size();
					largestRegion = region;
				}
			}
			
			return largestRegion;
		}
		
		return null;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		
		// Now recolor the regions in it
		for (ArrayList<Point> region : regions) {
			int random = (int) (Math.random() * 16777216);
			
			for (Point point : region) {
				recoloredImage.setRGB((int)point.getX(), (int)point.getY(), new Color(random).getRGB());
			}
		}
	}
	
	public void recolorImage(Color color) {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		
		// Now recolor the regions in it
		if (regions != null) {
			for (ArrayList<Point> region : regions) {
				if (region != null) {
					for (Point point : region) {
						if (point != null) {
							recoloredImage.setRGB((int)point.getX(), (int)point.getY(), color.getRGB());
		
						}
					}
				}
				
			}
		
		}
	}
}
