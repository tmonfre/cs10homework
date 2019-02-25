import java.util.*;

public class ALQueue<T> implements GenericQueue<T>{
	private ArrayList<T> queue;
	
	public ALQueue() {
		queue = new ArrayList<T>();
	}
	
	public void enqueue(T item) {
		queue.add(item);
	}
	
	public T dequeue() {
		T item = queue.get(0);
		queue.remove(0);
		return item;
	}

}
