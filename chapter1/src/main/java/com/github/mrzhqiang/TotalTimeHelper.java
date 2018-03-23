package com.github.mrzhqiang;

import java.util.concurrent.TimeUnit;

public class TotalTimeHelper {

  private final long startTime;
  private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

  public static TotalTimeHelper of(String info) {
    return new TotalTimeHelper(info);
  }

  private TotalTimeHelper(String info) {
    this.startTime = System.nanoTime();
    System.out.println(info);
  }

  public void total() {
    System.out.printf("用时(ms): %d%n", timeUnit.toMillis(System.nanoTime() - startTime));
    System.out.println();
  }
}
