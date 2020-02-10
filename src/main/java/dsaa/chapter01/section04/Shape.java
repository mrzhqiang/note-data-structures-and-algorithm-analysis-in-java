package dsaa.chapter01.section04;

import java.util.Collection;

public abstract class Shape implements Comparable<Shape> {
  public abstract double volume();

  public static double totalArea(Collection<? extends Shape> arr) {
    double total = 0;
    for (Shape s : arr) {
      if (s != null) {
        total += s.volume();
      }
    }
    return total;
  }
}
