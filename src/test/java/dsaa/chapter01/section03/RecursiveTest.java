package dsaa.chapter01.section03;

import org.junit.Test;

public class RecursiveTest {
  @Test
  public void testF() {
    System.out.println(Recursive.f(0));
    System.out.println(Recursive.f(1));
    System.out.println(Recursive.f(2));
    System.out.println(Recursive.f(3));
    System.out.println(Recursive.f(4));

    System.out.println(Recursive.f(25));
  }

  @Test
  public void testPrintOut() {
    Recursive.printOut(76234);
  }

  @Test
  public void testMod() {
    System.out.println(Recursive.mod(1016, 100));
  }

  @Test
  public void testPrintF() {
    // 统计 斐波那契数列 的总和，输入的参数为数列 0,1,2,...,k 中的 k
    Recursive.countF(100);
  }

}
