
public interface GenericQueue<T> {
	/* 
	 * add to the queue
	 */
	public void enqueue(T item);
	
	/*
	 * remove from the queue and return
	 */
	public T dequeue();
}
