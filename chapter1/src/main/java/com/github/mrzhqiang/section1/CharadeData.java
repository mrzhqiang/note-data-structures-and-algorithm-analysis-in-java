package com.github.mrzhqiang.section1;

public class CharadeData {
  private final char[][] data = {
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

      // 列，垂直方向：检查单词长度是否在列长度范围内，不在不用考虑
      if (wordSize <= data.length) {
        for (int i = 0; i < data.length; i++) {
          for (int j = 0; j < data[i].length; j++) {

          }
        }
      }

      // 定位到每一行
      for (int i = 0; i < data.length; i++) {
        char[] line = data[i];

        // 检查单词长度是否在行长度的范围内
        if (wordSize <= line.length) {
          int range = line.length - wordSize;

          if (range == 0 && word.equals(new String(line))) {
            System.out.println("find " + word + " in line " + i);
            break;
          }

          for (int j = 0; j < range; j++) {
            char[] copyRow = new char[wordSize];
            System.arraycopy(line, j, copyRow, 0, wordSize);

            if (word.equals(new String(copyRow))) {
              System.out.println(
                  "find forward horizontal: "
                      + word
                      + " in line "
                      + i
                      + " and from "
                      + j
                      + " to "
                      + (j + wordSize));
            }
          }
        }

        // 遍历列
        for (int j = 0; j < line.length; j++) {
          char row = line[j];
          // 水平方向

        }
      }
    }
  }

  private int findCharIndex(char[] line, char c) {
    for (int i = 0; i < line.length; i++) {
      if (line[i] == c) {
        return i;
      }
    }
    return -1;
  }
}
