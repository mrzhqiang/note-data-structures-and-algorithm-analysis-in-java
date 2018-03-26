package com.github.mrzhqiang.section4;

import org.junit.Before;
import org.junit.Test;

public class MemoryCellTest {

  private MemoryCell memoryCell;

  @Before
  public void generate() {
    memoryCell = new MemoryCell();
  }

  @Test
  public void testMemoryCell() {
    memoryCell.write("37");
    System.out.println("Contents are: " + memoryCell.read());
    memoryCell.write(11);
    System.out.println(memoryCell.read());
  }
}
