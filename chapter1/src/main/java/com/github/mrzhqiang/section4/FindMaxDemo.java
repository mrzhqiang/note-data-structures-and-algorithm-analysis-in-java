package com.github.mrzhqiang.section4;

public class FindMaxDemo {
  public static Comparable findMax(Comparable[] arr) {
    int maxIndex = 0;
    for (int i = 0; i < arr.length; i++) {
      // 警告的地方，是由于没有给泛型，书中代码如此，故不作调整
      if (arr[i].compareTo(arr[maxIndex]) > 0) {
        maxIndex = i;
      }
    }
    return arr[maxIndex];
  }
}
