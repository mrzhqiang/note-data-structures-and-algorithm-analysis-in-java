package dsaa.chapter01.exercise;

import java.util.Arrays;

/**
 * 习题1.6
 * 第一个例程是驱动程序，调用第二个例程并显示字符的所有排列，第二个使用递归。
 */
public class StringPermute {

  public static void permute(String string) {
    // 网上抄袭的答案，只对abc的情况有效
    permute(string.toCharArray(), 0, string.length());
    permute(new StringBuilder(string).reverse().toString().toCharArray(), 0, string.length());
  }

  private static void permute(char[] chars, int low, int high) {
    if (low == high) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    for (int i = low; i < high; i++) {
      builder.append(chars[i]);
    }
    int count = chars.length - builder.length();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        builder.append(chars[i]);
      }
    }
    System.out.println(builder.toString());
    permute(chars, ++low, high);
  }

  public static void permuteOther(String src) {
    // 最外层属于纯粹的置零交换，即：逐个与原字符串的第一个字符交换位置。
    for (int i = 0; i < src.length(); i++) {
      char[] chars = src.toCharArray();
      swapToIndex(chars, 0, i);
      System.out.println(Arrays.toString(chars));
      permuteOther(chars, 1, chars.length);
    }
  }

  private static void permuteOther(char[] chars, int low, int high) {
    // 这里属于后面几层的位置交换和打印
    for (int i = low; i < high; i++) {
      char[] c = chars.clone();
      // 不同的位置才需要交换，相同的直接递归到下一层
      if (i != low) {
        swapToIndex(c, low, i);
        System.out.println(Arrays.toString(c));
      }
      permuteOther(c, low + 1, c.length);
    }
  }

  private static void swapToIndex(char[] chars, int first, int second) {
    char temp = chars[first];
    chars[first] = chars[second];
    chars[second] = temp;
  }
}
