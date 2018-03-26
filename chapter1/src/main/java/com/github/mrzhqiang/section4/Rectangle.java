package com.github.mrzhqiang.section4;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;

public class Rectangle extends Shape {
  private final double width;
  private final double height;

  public Rectangle(double width, double height) {
    this.width = width;
    this.height = height;
  }

  @Override public double volume() {
    return width * height;
  }

  @Override public int compareTo(@Nonnull Shape o) {
    return Double.compare(volume(), o.volume());
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("width", width)
        .add("height", height)
        .toString();
  }
}
