package dsaa.section1;

public final class CharadeData {
  private CharadeData() {
    // no instance
  }

  private static final char[][] datas = {
      {'t', 'h', 'i', 's'},
      {'w', 'a', 't', 's'},
      {'o', 'a', 'h', 'g'},
      {'f', 'g', 'd', 't'}
  };

  private static final String[] words = {"this", "two", "fat", "that"};

  public static void verify() {
    // 取得单词表中的每一个单词
    for (String word : words) {
      int wordLength = word.length();

      for (int i = 0; i < datas.length; i++) {
        char[] data = datas[i];
        for (int j = 0; j < data.length; j++) {
          char c = data[j];
          // 必须首字母匹配
          if (c == word.charAt(0)) {
            if (data.length - j >= wordLength) {
              // 每一行的长度，减去当前下标，如果大于等于单词的长度，那么表示有足够空间展开字符来比对
              // 水平正方向
              String d = new String(data, 0, wordLength);
              if (d.equals(word)) {
                System.out.printf("在水平正方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, i, wordLength - 1,
                    d);
                System.out.println();
                break;
              }
            } else if (j + 1 >= wordLength) {
              // 每一行中的当前下标加上1，大于等于单词的长度，说明可以逆序得到一个全新的字符串来比对
              // 水平反方向
              char[] chars = new char[wordLength];
              for (int k = 0, l = j; k < wordLength; k++, l--) {
                chars[k] = data[l];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在水平反方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, i,
                    j - wordLength + 1,
                    d);
                System.out.println();
                break;
              }
            }

            if (datas.length - i >= wordLength) {
              // 垂直正方向
              char[] chars = new char[wordLength];
              for (int k = 0, l = i; k < wordLength; k++, l++) {
                chars[k] = datas[l][j];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在垂直正方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordLength - 1, j,
                    d);
                System.out.println();
                break;
              }
            } else if (i + 1 >= wordLength) {
              // 垂直反方向
              char[] chars = new char[wordLength];
              for (int k = 0, l = i; k < wordLength; k++, l--) {
                chars[k] = datas[l][j];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在垂直反方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j,
                    i - wordLength + 1,
                    j,
                    d);
                System.out.println();
                break;
              }
            }

            if (datas.length - i >= wordLength) {
              if (data.length - j >= wordLength) {
                // 对角正顺方向
                char[] chars = new char[wordLength];
                for (int k = 0, l = i, m = j; k < wordLength; k++, l++, m++) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角正顺方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordLength - 1,
                      wordLength - 1,
                      d);
                  System.out.println();
                  break;
                }
              } else if (j + 1 >= wordLength) {
                // 对角正逆方向
                char[] chars = new char[wordLength];
                for (int k = 0, l = i, m = j; k < wordLength; k++, l++, m--) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角正逆方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordLength - 1,
                      j - wordLength + 1,
                      d);
                  System.out.println();
                  break;
                }
              }
            } else if (i + 1 >= wordLength) {
              if (j + 1 >= wordLength) {
                // 对角反逆方向
                char[] chars = new char[wordLength];
                for (int k = 0, l = i, m = j; k < wordLength; k++, l--, m--) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角反逆方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j,
                      i - wordLength + 1,
                      j - wordLength + 1,
                      d);
                  System.out.println();
                  break;
                }
              } else if (data.length - j >= wordLength) {
                // 对角反顺方向
                char[] chars = new char[wordLength];
                for (int k = 0, l = i, m = j; k < wordLength; k++, l--, m++) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角反顺方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j,
                      i - wordLength + 1,
                      wordLength - 1,
                      d);
                  System.out.println();
                  break;
                }
              }
            }
          }
        }
      }
    }
  }
}
