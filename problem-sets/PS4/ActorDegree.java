/**
 * This is a container class that holds an actor and a value (degree) associated with them
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 *
 */
public class ActorDegree {
	public String name;
	public double degree;
	
	// constructor method - stores name and value
	public ActorDegree(String s, double i) {
		name = s;
		degree = i;
	}
	
	// GETTERS AND SETTERS
	public String getName() {
		return name;
	}

	public double getDegree() {
		return degree;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public void setDegree(int i) {
		degree = i;
	}
	
	@Override
	public String toString() {
		return "Actor name: " + name + ", Degree: " + degree;
	}	
}
