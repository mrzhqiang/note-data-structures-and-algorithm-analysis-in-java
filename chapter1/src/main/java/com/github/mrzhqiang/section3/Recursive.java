package com.github.mrzhqiang.section3;

public class Recursive {
  private Recursive() {
    // no instance
  }

  public static int f(int x) {
    if (x == 0) {
      return 0;
    } else {
      return 2 * f(x - 1) + x * x;
    }
  }

  public static void printOut(int n) {
    if (n >= 10) {
      printOut(n / 10);
    }
    System.out.print(n % 10);
  }

  public static int mod(int n, int mod) {
    return n - (n / mod) * mod;
  }

  public static void countF(int f) {
    int i = 0;
    int sum = 0;
    while (i <= f) {
      sum += i++;
    }
    System.out.println(sum);
  }

}
