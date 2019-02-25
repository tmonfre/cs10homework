import java.awt.*;

public class TestPrint {
	
	public static void main(String[] args) {
		Polyline s = new Polyline(new Point(1,2),Color.black);
		
		s.addPoint(new Point(3,4));
		s.addPoint(new Point(5,6));
		s.addPoint(new Point(7,8));
		s.addPoint(new Point(9,10));
		
		String test = s.toString();
		
		String[] testList = test.split("\\ ");
		
		for (String sw : testList) {
			System.out.println(sw);
		}
		
		System.out.println(testList);
	}

}
