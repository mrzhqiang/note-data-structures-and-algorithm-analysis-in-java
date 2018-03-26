package com.github.mrzhqiang.section5;

import org.junit.Test;

public class BoxingDemoTest {
  @Test
  public void testBoxing() {
    GenericMemoryCell<Integer> m = new GenericMemoryCell<>();
    m.write(37);
    int val = m.read();
    System.out.println("Contents are: " + val);
  }
}
