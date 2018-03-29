package com.github.mrzhqiang.section4;

import com.github.mrzhqiang.TotalTimeHelper;
import org.junit.Before;
import org.junit.Test;

public class SubTest {

  private int[] data;

  @Before
  public void generateData() {
    data = new int[5000];
    for (int i = 0; i < 5000; i++) {
      if (i % 2 == 0) {
        data[i] = (int) (Math.random() * 10);
      } else {
        data[i] = -(int) (Math.random() * 10);
      }
    }
  }

  @Test
  public void testMaxSubSum1() {
    long time = System.currentTimeMillis();
    System.out.println("最大子序列和为：" + Sub.maxSubSum1(data));

    long endTime = System.currentTimeMillis();

    // 9989 ms
    System.out.println("Use time(ms): " + (endTime - time));
  }

  @Test
  public void testMaxSubSum2() {
    long time = System.currentTimeMillis();

    System.out.println("最大子序列和为：" + Sub.maxSubSum2(data));

    long endTime = System.currentTimeMillis();

    // 11 ms
    System.out.println("Use time(ms): " + (endTime - time));
  }

  @Test
  public void testMaxSubSum3() {
    long time = System.currentTimeMillis();

    System.out.println("最大系序列和为： " + Sub.maxSubSum3(data));

    long endTime = System.currentTimeMillis();

    // 1 ms
    System.out.println("Use time(ms): " + (endTime - time));
  }

  @Test
  public void testMaxSubSum4() {
    TotalTimeHelper helper = TotalTimeHelper.of("测试最大子序列和。");

    System.out.println(Sub.maxSubSum4(data));

    helper.total();
  }
}
