package dsaa.exercise;

/**
 * 习题1.5
 * 使用递归，统计 N 的二进制表示中 1 的个数。
 */
public class FindOneCount {

  public static int find(int n) {
    if (n <= 0) {
      return 0;
    }

    int count = 0;
    if (n % 2 == 1) {
      count += find(n / 2) + 1;
    } else {
      count += find(n / 2);
    }
    return count;
  }
}
