package com.github.mrzhqiang.section4;

import com.google.common.base.MoreObjects;

public class Square extends Rectangle {

  public Square(double sides) {
    super(sides, sides);
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sides", getWidth())
        .toString();
  }
}
