/**
 * defines object that stores a character and its frequency in a binary tree
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class CharFreqElement {
	// instance variables for character and frequency
	char character;
	int frequency;
	
	// constructor - just set character and frequency
	public CharFreqElement(char c, int f) {
		character = c;
		frequency = f;
	}
	
	// getter and setter methods
	public char getCharacter() {
		return character;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void setCharacter(char c) {
		character = c;
	}
	
	public void setFrequency(int f) {
		frequency = f;
	}
	
	/**
	 * override toString method
	 */
	@Override
	public String toString() {
		return "character: " + character + ", frequency: " + frequency;
	}
}
