package dsaa.section4;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;

public class Circle extends Shape {
  private final double radius;

  public Circle(double radius) {
    this.radius = radius;
  }

  @Override public int compareTo(@Nonnull Shape o) {
    return Double.compare(volume(), o.volume());
  }

  @Override public double volume() {
    return radius * radius * Math.PI;
  }

  public double getRadius() {
    return radius;
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("radius", radius)
        .toString();
  }
}
