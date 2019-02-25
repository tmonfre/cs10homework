
public class SplitTest {

	public static void main(String[] args) {
		String string = "600|Nobody's Friend";
		
		String[] l = string.split("\\|");
		
		for (String s : l) {
			System.out.println(s);
		}
	}
	
}
