package dsaa.chapter01.exercise;

import org.junit.Test;

public class FindOneCountTest {

  @Test
  public void testFind() {
    // 10 >>> 1010
    // 100 >>> 0110 0100
    // 63 >>> 0011 1111
    System.out.println(FindOneCount.find(63));
  }
}
