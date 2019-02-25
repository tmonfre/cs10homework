public class DoublyLinkedCircularSentinel<T> implements SimpleList<T> {
	private Element sentinel;
	private int size;
	
	private class Element {
		private T data;
		private Element next;
		private Element prev;
		
		private Element(T data) {
			this.data = data;
		}
	}
	
	public DoublyLinkedCircularSentinel() {
		sentinel = new Element(null);
		sentinel.next = sentinel;
		sentinel.prev = sentinel;
		size = 0;
	}
	
	private Element advance(int n) {
		Element e = sentinel;
		while (n > 0) {
			e = e.next;
			n--;
		}
		
		return e;
	}
	
	public void add(int idx, T item) throws Exception {
		if (idx > size || (idx < 0)) {
			throw new Exception("invalid index");
		}
		
		Element e = advance(idx);
		Element toAdd = new Element(item);
		
		toAdd.next = e.next;
		toAdd.next.prev = toAdd;
		toAdd.prev = e;
		e.next = toAdd;
		
		size++;
	}
	
	public void remove(int idx) throws Exception {
		if (idx > size || idx < 0) {
			throw new Exception("invalid index");
		}
		
		Element e = advance(idx);
		e.next = e.next.next;
		e.next.prev = e;
		
		size--;
	}
	
	public void set(int idx, T item) throws Exception {
		if (idx > size || idx < 0) {
			throw new Exception("invalid index");
		}
		
		Element e = advance(idx + 1);
		e.data = item;
	}
	
	public T get(int idx) throws Exception {
		if (idx > size || idx < 0) {
			throw new Exception("invalid index");
		}
		
		Element e = advance(idx + 1);
		return e.data;
	}
	
	public int size() {
		return size;
	}

}
