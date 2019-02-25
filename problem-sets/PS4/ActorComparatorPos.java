import java.util.Comparator;
/**
 * comparator class that implements interface Comparator
 * implements compare method so priority queue can compare ActorDegree objects
 * 
 * this class compares positive values
 * 
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class ActorComparatorPos implements Comparator<ActorDegree> {
	/**
	 * implement compare method from Comparator interface
	 * comparison is based on degree of each actor in the tree
	 */
	public int compare(ActorDegree a1, ActorDegree a2) {
		if (a1.getDegree() < a2.getDegree()) return -1;
		else if (a1.getDegree() == a2.getDegree()) return 0;
		else return 1;
	}	
}

