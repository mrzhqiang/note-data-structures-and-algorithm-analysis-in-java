package com.github.mrzhqiang.section4;

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

