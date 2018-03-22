package com.github.mrzhqiang.section1;

import org.junit.Before;
import org.junit.Test;

/** 选择问题：有一组N个数，书中假设的是3000w个，要确定其中第k个最大者。 */
public class SelectionProblemTest {

  private SelectionProblemData selectionProblemData;

  @Before
  public void create() {
    selectionProblemData = new SelectionProblemData();
  }

  @Test
  public void testBubbleSort() {
    // 使用冒泡排序，书中描述说需要若干天时间，测试有半个小时未完，确信会消耗大量时间
    // 30000消耗了700ms左右，300000消耗了80000ms左右
    selectionProblemData.bubbleSort();
  }

  @Test
  public void testBetterSort() {
    // 再使用“稍微好一点的算法”
    // 30000消耗了200ms左右，300000消耗了21000ms左右
    selectionProblemData.betterSort();
  }
}
