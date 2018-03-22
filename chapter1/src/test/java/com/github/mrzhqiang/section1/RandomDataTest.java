package com.github.mrzhqiang.section1;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class RandomDataTest {

  private RandomData randomData;

  @Before
  public void createRandomData() {
    randomData = new RandomData();
  }

  @Test
  public void testGenerateData() {
    System.out.println("generate data begin.");
    long time = System.nanoTime();
    randomData.generateData();
    long nanoTime = System.nanoTime() - time;
    System.out.println("total time(ms): " + TimeUnit.NANOSECONDS.toMillis(nanoTime));
  }

  @Test
  public void generateAndWriteToFile() {
    testGenerateData();
    long time = System.nanoTime();
    System.out.println("write data begin.");
    try {
      randomData.write(new File("../chapter1/src/main/resource/data.dat"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    long nanoTime = System.nanoTime() - time;
    System.out.println("total time(ms): " + TimeUnit.NANOSECONDS.toMillis(nanoTime));
  }
}
