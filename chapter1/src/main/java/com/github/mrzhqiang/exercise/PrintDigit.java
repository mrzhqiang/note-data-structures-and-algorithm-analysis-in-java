package com.github.mrzhqiang.exercise;

import java.math.BigDecimal;

/**
 * 习题1.3
 * 只是用处理单个数字的 printDigit 方法，输入任意的 double 型量，允许负数。
 *
 * PS：有一个陷阱在于，若只在一个方法中处理，会显得很复杂，而剥离整数部分和浮点数部分，则特别简单。
 */
public class PrintDigit {

  public static void printDouble(double value) {
    if (value < 0) {
      System.out.print("-");
      printDouble(Math.abs(value));
      return;
    }

    int v = (int) value;
    printInt(v);

    System.out.print(".");
    // value - v
    double decimal = BigDecimal.valueOf(value).subtract(BigDecimal.valueOf(v)).doubleValue();
    // 0.01 >>> 01, length => 2
    int length = String.valueOf(decimal).replace("0.", "").length();

    while (length > 0) {
      decimal = decimal * 10;
      length--;
      printDigit((int) decimal);
    }
  }

  private static void printInt(int i) {
    if (i >= 10) {
      printInt(i / 10);
    }
    printDigit(i % 10);
  }

  private static void printDigit(int n) {
    System.out.print(n);
  }
}
