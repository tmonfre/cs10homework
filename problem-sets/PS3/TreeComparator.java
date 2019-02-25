import java.util.Comparator;
/**
 * comparator class that implements interface Comparator
 * implements compare method so priority queue can compare objects
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */
public class TreeComparator implements Comparator<BinaryTree<CharFreqElement>> {
	/**
	 * implement compare method from Comparator interface
	 * comparison is based on frequencies of each character in the tree - higher frequency indicates greater
	 */
	public int compare(BinaryTree<CharFreqElement> t1, BinaryTree<CharFreqElement> t2) {
		if (t1.getData().getFrequency() < t2.getData().getFrequency()) return -1;
		else if (t1.getData().getFrequency() == t2.getData().getFrequency()) return 0;
		else return 1;
	}
}