package com.github.mrzhqiang.section1;

import org.junit.Test;

/**
 * 习题 1.1：【选择问题】。
 * 有一组N个数，书中假设的是 3000w 个，要确定其中第 (k=N/2) 个最大者。
 */
public class SelectionProblemTest {

  @Test
  public void testBubbleSort() {
    // 使用冒泡排序，书中描述说，3000w条数据，需要若干天时间

    // 13 ms
    SelectionProblemData selectionProblemData = new SelectionProblemData(3000);
    selectionProblemData.bubbleSort();

    // 1116 ms
    selectionProblemData = new SelectionProblemData(30000);
    selectionProblemData.bubbleSort();

    // 97199 ms
    selectionProblemData = new SelectionProblemData(300000);
    selectionProblemData.bubbleSort();
  }

  @Test
  public void testBetterSort() {
    // 再使用“稍微好一点的算法”。不过，从时间值来看，并没有优化多少。

    // 11 ms
    SelectionProblemData selectionProblemData = new SelectionProblemData(3000);
    selectionProblemData.bubbleSort();

    // 1150 ms
    selectionProblemData = new SelectionProblemData(30000);
    selectionProblemData.bubbleSort();

    // 97008 ms
    selectionProblemData = new SelectionProblemData(300000);
    selectionProblemData.bubbleSort();
  }
}
