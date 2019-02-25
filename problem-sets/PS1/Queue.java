import java.util.*;
import java.awt.*;

/**
 * @author Thomas Monfre, Winter 2018, wrote basic queue class
 */

public class Queue<T> {
	
	private ArrayList<T> queue;
	
	public Queue() {
		queue = new ArrayList<T>();
	}
	
	public void enqueue(T point) {
		queue.add(point);
	}
	
	public T dequeue() {
		T point = queue.get(0);
		queue.remove(0);
		return point;
	}
	
	public int getLength() {
		return queue.size();
	}
	
	public String toString() {
		return queue.toString();
	}

}
