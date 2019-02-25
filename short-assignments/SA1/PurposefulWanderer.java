import java.awt.*;

/**
 * This subclass of Blob wanders more purposefully than an object of subclass Wanderer.
 * @author Thomas Monfre
 * Dartmouth College CS 10, Winter 2018
 */

public class PurposefulWanderer extends Blob {
	
	/**
	 * @param stepsRequired			the number of steps the blob should take before picking 
	 * 								new random dx and dy values
	 * 
	 * @param count					static variable (not instance variable) that simply 
	 * 								keeps track of the blob's progress in wandering 
	 * 								before taking new random dx and dy values
	 * 
	 * 								count takes an initial value of 9 (and is later 
	 * 								reset to 9 when meeting stepsRequired) such 
	 * 								that each NewWanderer blob will move
	 */
	
	private int stepsRequired;
	private static double count = 9;
	
	public PurposefulWanderer(double x, double y) {
		super(x,y);
		stepsRequired = (int) (10 + (Math.random() * 10));
	}
	
	public PurposefulWanderer(double x, double y, Color c) {
		//super(x,y,c);
		stepsRequired = (int) (10 + (Math.random() * 10));
	}
	
	public PurposefulWanderer(double x, double y, double r, Color c) {
		//super(x,y,r,c);
		stepsRequired = (int) (10 + (Math.random() * 10));
	}

	/** 
	 * this method overrides the step method in the superclass
	 * this method increases count, then picks new random values for dx and dy if count
	 * meets the number of required steps (resets count if this occurs)
	 * 
	 * this method updates the x and y location of the blob after (possibly) altering dx and dy
	 */
	
	@Override
	public void step( ) { 
		count++;
		
		if(count == stepsRequired) {
			dx = 2 * (Math.random()-0.5);
			dy = 2 * (Math.random()-0.5);
			count = 9;
		}

		x += dx;
		y += dy;
	}
}
