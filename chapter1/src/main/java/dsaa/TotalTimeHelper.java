package dsaa;

import java.util.concurrent.TimeUnit;

public class TotalTimeHelper {

  private final String name;
  private final long startTime;
  private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

  public static TotalTimeHelper of(String info) {
    return new TotalTimeHelper(info);
  }

  private TotalTimeHelper(String name) {
    this.name = name;
    this.startTime = System.nanoTime();
  }

  public String total() {
    return String.format("%s 用时(ms): %d%n", name, timeUnit.toMillis(System.nanoTime() - startTime));
  }
}
