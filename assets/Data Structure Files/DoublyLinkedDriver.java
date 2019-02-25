
public class DoublyLinkedDriver {
	
	public static void main(String[] args) throws Exception {
		DoublyLinked<Integer> list = new DoublyLinked<Integer>();
		
		for (int i=0; i<10; i++) {
			list.add(i, (Integer) i);
		}
		
		System.out.println(list);
	}

}
