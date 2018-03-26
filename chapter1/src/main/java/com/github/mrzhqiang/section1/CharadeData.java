package com.github.mrzhqiang.section1;

public class CharadeData {
  private final char[][] datas = {
      {'t', 'h', 'i', 's'},
      {'w', 'a', 't', 's'},
      {'o', 'a', 'h', 'g'},
      {'f', 'g', 'd', 't'}
  };

  private final String[] words = {"this", "two", "fat", "that"};

  public void verify() {
    // 取得单词表中的每一个单词
    for (String word : words) {
      int wordSize = word.length();

      for (int i = 0; i < datas.length; i++) {
        char[] data = datas[i];
        for (int j = 0; j < data.length; j++) {
          char c = data[j];
          // 必须首字母匹配
          if (c == word.charAt(0)) {
            if (data.length - j >= wordSize) {
              // 水平正方向
              String d = new String(data, 0, wordSize);
              if (d.equals(word)) {
                System.out.printf("在水平正方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, i, wordSize - 1,
                    d);
                System.out.println();
                break;
              }
            } else if (j + 1 >= wordSize) {
              // 水平反方向
              char[] chars = new char[wordSize];
              for (int k = 0, l = j; k < wordSize; k++, l--) {
                chars[k] = data[l];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在水平反方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, i,
                    j - wordSize + 1,
                    d);
                System.out.println();
                break;
              }
            }

            if (datas.length - i >= wordSize) {
              // 垂直正方向
              char[] chars = new char[wordSize];
              for (int k = 0, l = i; k < wordSize; k++, l++) {
                chars[k] = datas[l][j];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在垂直正方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordSize - 1, j,
                    d);
                System.out.println();
                break;
              }
            } else if (i + 1 >= wordSize) {
              // 垂直反方向
              char[] chars = new char[wordSize];
              for (int k = 0, l = i; k < wordSize; k++, l--) {
                chars[k] = datas[l][j];
              }
              String d = new String(chars);
              if (d.equals(word)) {
                System.out.printf("在垂直反方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, i - wordSize + 1,
                    j,
                    d);
                System.out.println();
                break;
              }
            }

            if (datas.length - i >= wordSize) {
              if (data.length - j >= wordSize) {
                // 对角正顺方向
                char[] chars = new char[wordSize];
                for (int k = 0, l = i, m = j; k < wordSize; k++, l++, m++) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角正顺方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordSize - 1,
                      wordSize - 1,
                      d);
                  System.out.println();
                  break;
                }
              } else if (j + 1 >= wordSize) {
                // 对角正逆方向
                char[] chars = new char[wordSize];
                for (int k = 0, l = i, m = j; k < wordSize; k++, l++, m--) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角正逆方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j, wordSize - 1,
                      j - wordSize + 1,
                      d);
                  System.out.println();
                  break;
                }
              }
            } else if (i + 1 >= wordSize) {
              if (j + 1 >= wordSize) {
                // 对角反逆方向
                char[] chars = new char[wordSize];
                for (int k = 0, l = i, m = j; k < wordSize; k++, l--, m--) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角反逆方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j,
                      i - wordSize + 1,
                      j - wordSize + 1,
                      d);
                  System.out.println();
                  break;
                }
              } else if (data.length - j >= wordSize) {
                // 对角反顺方向
                char[] chars = new char[wordSize];
                for (int k = 0, l = i, m = j; k < wordSize; k++, l--, m++) {
                  chars[k] = datas[l][m];
                }
                String d = new String(chars);
                if (d.equals(word)) {
                  System.out.printf("在对角反顺方向上，找到从 (%d, %d) 到 (%d, %d) 的 %s%n", i, j,
                      i - wordSize + 1,
                      wordSize - 1,
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
