package dsaa.section4;

import org.junit.Test;

public class FindMaxDemoTest {
  @Test
  public void testFindMax() {
    Shape[] sh1 = {
        new Circle(2.0),
        new Square(3.0),
        new Rectangle(3.0, 4.0)
    };

    System.out.println(FindMaxDemo.findMax(sh1));

    String[] st1 = {"Joe", "Bob", "Bill", "Zeke"};
    System.out.println(FindMaxDemo.findMax(st1));
  }
}
