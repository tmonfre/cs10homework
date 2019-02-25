
public class Test {
	
	public static void main(String[] args) {
		String testOriginal = "inputs/WarAndPeace.txt";
	
		testOriginal = testOriginal.substring(0, testOriginal.length() - 4);
		
		testOriginal += "_compressed.txt";
		
		System.out.println(testOriginal);
		
		String s = "test";
		String s1 = s;
		
		s1 += "!!";
		
		System.out.println(s);
		System.out.println(s1);
		
		
	}

}
