package dsaa.chapter02;

/** 2.4.3 最大子序列和问题的求解 */
public class Sub {
  private Sub() {
    // no instance
  }

  /** 这个方法从 0 到 N 逐个遍历，同时以每一个数为起点，求和所有的子序列。 */
  public static int maxSubSum1(int[] a) {
    int maxSum = 0;

    for (int i = 0; i < a.length; i++) {
      for (int j = i; j < a.length; j++) {
        int thisSum = 0;

        for (int k = i; k <= j; k++) {
          thisSum += a[k];
        }

        if (thisSum > maxSum) {
          maxSum = thisSum;
        }
      }
    }

    return maxSum;
  }

  public static int maxSubSum2(int[] a) {
    int maxSum = 0;
    for (int i = 0; i < a.length; i++) {
      int thisSum = 0;
      for (int j = i; j < a.length; j++) {
        thisSum += a[j];
        if (thisSum > maxSum) {
          maxSum = thisSum;
        }
      }
    }
    return maxSum;
  }

  public static int maxSubSum3(int[] a) {
    return maxSumRec(a, 0, a.length - 1);
  }

  private static int maxSumRec(int[] a, int left, int right) {
    if (left == right) {
      if (a[left] > 0) {
        return a[left];
      } else {
        return 0;
      }
    }

    int center = (left + right) / 2;
    int maxLeftSum = maxSumRec(a, left, center);
    int maxRightSum = maxSumRec(a, center + 1, right);

    int maxLeftBorderSum = 0, leftBorderSum = 0;

    for (int i = center; i >= left; i--) {
      leftBorderSum += a[i];
      if (leftBorderSum > maxLeftBorderSum) {
        maxLeftBorderSum = leftBorderSum;
      }
    }

    int maxRightBorderSum = 0, rightBorderSum = 0;

    for (int i = center + 1; i <= right; i++) {
      rightBorderSum += a[i];
      if (rightBorderSum > maxRightBorderSum) {
        maxRightBorderSum = rightBorderSum;
      }
    }

    return max3(maxLeftSum, maxRightSum, maxLeftBorderSum + maxRightBorderSum);
  }

  private static int max3(int first, int second, int third) {
    return Math.max(first, Math.max(second, third));
  }

  public static int maxSubSum4(int[] a) {
    int maxSum = 0;
    int thisSum = 0;

    for (int anA : a) {
      thisSum += anA;

      if (thisSum > maxSum) {
        maxSum = thisSum;
      } else if (thisSum < 0) {
        thisSum = 0;
      }
    }

    return maxSum;
  }
}
