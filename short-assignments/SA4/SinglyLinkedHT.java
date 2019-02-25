/**
 * A singly-linked list implementation of the SimpleList interface
 * Now with tail pointer
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012, 
 * based on a more feature-ful version by Tom Cormen and Scot Drysdale
 * @author CBK, Spring 2016, cleaned up inner class, extended testing
 * @author Thomas Monfre, Winter 2018, added tail
 */
public class SinglyLinkedHT<T> implements SimpleList<T> {
	private Element head;	// front of the linked list
	private Element tail;	// end
	private int size;		// # elements in the list

	/**
	 * The linked elements in the list: each has a piece of data and a next pointer
	 */
	private class Element {
		private T data;
		private Element next;

		private Element(T data, Element next) {
			this.data = data;
			this.next = next;
		}
	}

	public SinglyLinkedHT() {
		head = null;
		tail = null;
		size = 0;
	}

	public int size() {
		return size;
	}

	/**
	 * Helper function, advancing to the nth Element in the list and returning it
	 * (exception if not that many elements)
	 */
	private Element advance(int n) throws Exception {
		Element e = head;
		while (n > 0) {
			// Just follow the next pointers
			e = e.next;
			if (e == null) throw new Exception("invalid index");
			n--;
		}
		return e;
	}

	public void add(int idx, T item) throws Exception {        
		if (idx == 0) {
			// Insert at head
			head = new Element(item, head);
			
			// if this is the first element in the list, set the tail as the first element (what the head points to)
			if(head.next == null) {
				tail = head;
			}
		}
		else {
			// It's the next thing after element # idx-1
			Element e = advance(idx-1);
			// Splice it in
			e.next = new Element(item, e.next);
			
			// update tail if the added Element is the last in the list
			if(e.next.next == null) {
				tail = e.next;
			}
		}
		size++;
	}

	public void remove(int idx) throws Exception {
		if (idx == 0) {
			// Just pop off the head
			if (head == null) throw new Exception("invalid index");
			head = head.next;
			
			// if just removed the only element, set the tail to null
			if (head == null) {
				tail = null;
			}
		}
		else {
			// It's the next thing after element # idx-1
			Element e = advance(idx-1);
			if (e.next == null) throw new Exception("invalid index");
			// Splice it out
			e.next = e.next.next;
			
			// update tail if the removed Element was the last in the list
			if(e.next == null) {
				tail = e;
			}
		}
		size--;
	}

	public T get(int idx) throws Exception {
		Element e = advance(idx);
		return e.data;
	}

	public void set(int idx, T item) throws Exception {
		Element e = advance(idx);
		e.data = item;
	}

	/**
	 * Appends other to the end of this in constant time, by manipulating head/tail pointers
	 */
	public void append(SinglyLinkedHT<T> other) {
		// if second list is empty, just update its head and tail
		if (other.size() == 0) {
			other.head = this.head;
			other.tail = this.tail;
			
			// update second list's size
			other.size = this.size();
		}

		// if first list is empty, just update its head and tail
		else if (this.size() == 0) {
			this.head = other.head;
			this.tail = other.tail;
			
			// update first list's size
			this.size = other.size();
		}
		
		// set first list's tail to second list's head, update first tail to second tail and second head to first head
		else {
			this.tail.next = other.head;
			other.head = this.head;
			this.tail = other.tail;
			
			// update both list's size
			int temp = this.size(); // temporarily storing original size of list 1
			this.size += other.size();
			other.size += temp;
			
		}
	
	}
	
	public String toString() {
		String result = "";
		for (Element x = head; x != null; x = x.next) 
			result += x.data + "->"; 
		result += "[/]";

		return result;
	}

	public static void main(String[] args) throws Exception {
		SinglyLinkedHT<String> list1 = new SinglyLinkedHT<String>();
		SinglyLinkedHT<String> list2 = new SinglyLinkedHT<String>();

		System.out.println(list1 + " + " + list2);
		list1.append(list2);
		System.out.println(" = " + list1);

		list2.add(0, "a");
		System.out.println(list1 + " + " + list2);
		list1.append(list2);
		System.out.println(" = " + list1);
		
		list1.add(1, "b");
		list1.add(2, "c");
		SinglyLinkedHT<String> list3 = new SinglyLinkedHT<String>();
		System.out.println(list1 + " + " + list3);
		list1.append(list3);
		System.out.println(" = " + list1);
		
		SinglyLinkedHT<String> list4 = new SinglyLinkedHT<String>();
		list4.add(0, "z");
		list4.add(0, "y");
		list4.add(0, "x");		
		System.out.println(list1 + " + " + list4);
		list1.append(list4);
		System.out.println(" = " + list1);
		
		list1.remove(5);
		list1.remove(4);
		SinglyLinkedHT<String> list5 = new SinglyLinkedHT<String>();
		list5.add(0, "z");
		list5.add(0, "y");
		System.out.println(list1 + " + " + list5);
		list1.append(list5);
		System.out.println(" = " + list1);
	}
}
