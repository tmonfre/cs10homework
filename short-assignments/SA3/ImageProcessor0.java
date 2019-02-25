import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * A class demonstrating manipulation of image pixels.
 * Version 0: just the core definition
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Winter 2014, rewritten for BufferedImage
 * @author CBK, Spring 2015, refactored to separate GUI from operations
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018, added emphasizeColor method
 */
public class ImageProcessor0 {
	private BufferedImage image;		// the current image being processed

	/**
	 * @param image		the original
	 */
	public ImageProcessor0(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * static method to determine which RGB value in a color is the greatest
	 * @param color -- color object
	 * @return 0 for red, 1 for green, or 2 for blue
	 */
	public static int topRGB(Color color) {
		int result = 0;
		
		if (color.getRed() >= color.getBlue() && color.getRed() >= color.getGreen()) {
			result = 0;
		}
		else if (color.getGreen() >= color.getRed() && color.getGreen() >= color.getBlue()) {
			result = 1;
		}
		else if (color.getBlue() >= color.getRed() && color.getBlue() >= color.getGreen()) {
			result = 2;
		}
		
		return result;
	}
	
	/**
	 * draws a square at an x and y location of based on radius and color it is given
	 * this code follows the same structure at the drawSquare method from the larger
	 * ImageProcessor class provided by Prof. Pierson
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 */
	public void drawSquare(int x, int y, int radius, Color color) {
		for (int newY = Math.max(0, y - radius); newY < Math.min(image.getHeight(), y + radius); newY++) {
			for (int newX = Math.max(0, x - radius); newX < Math.min(image.getWidth(), x + radius); newX++) {
				image.setRGB(newX, newY, color.getRGB());
			}
		}
	}
	
	/** 
	 * emphasizes the predominant color of the pixel at the given location
	 * by diminishing the non-predominant colors by 50% - Minecraft look
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 */
	public void emphasizeColor(int x, int y, int radius) {
		// retrieve the RGB values of the pixel location (x,y) of the mouse
		Color color = new Color(image.getRGB(x, y));
		
		// initialize integer values for red, green, and blue
		int red = 0;
		int green = 0;
		int blue = 0;
		
		// determine which RGB value is the highest at the location
		// reduce the lower two values by 50%, leave the highest at its value
		if (topRGB(color) == 0) {
			red = color.getRed();
			green = green / 2;
			blue = blue / 2;
		}
		else if (topRGB(color) == 1) {
			red = red / 2;
			green = color.getGreen();
			blue = blue / 2;
		}
		else {
			red = red / 2;
			green = green / 2;
			blue = color.getBlue();
		}
		
		// create a new Color object based on the predominant value
		Color result = new Color(red, green, blue);
		
		// paint a square with a radius provided by the user of the new color
		drawSquare(x, y, radius, result);
		
	}
	
}
