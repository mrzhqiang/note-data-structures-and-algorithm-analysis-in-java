package com.github.mrzhqiang.section1;

import com.github.mrzhqiang.TotalTimeHelper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class SelectionProblemData {

  private final File dataFile = new File("..\\data.dat");
  private final int dataLength = 30/*00_0000*/;
  private final Random random = new SecureRandom();

  private final int[] datas = new int[dataLength];
  private final int k = dataLength / 2;

  public SelectionProblemData() {
    TotalTimeHelper helper = TotalTimeHelper.of("创建【选择问题】数据。");
    try {
      create();
    } catch (IOException e) {
      e.printStackTrace();
    }
    helper.total();
  }

  public void bubbleSort() {
    TotalTimeHelper helper = TotalTimeHelper.of("开始冒泡排序。");
    for (int i = 0; i < datas.length; i++) {
      for (int j = 0; j < datas.length - 1 - i; j++) {
        if (datas[j] < datas[j + 1]) {
          int temp = datas[j];
          datas[j] = datas[j + 1];
          datas[j + 1] = temp;
        }
      }
    }
    System.out.println("find k data: " + datas[k - 1]);
    helper.total();
  }

  private void create() throws IOException {
    if (!dataFile.exists()) {
      if (dataFile.createNewFile()) {
        System.out.println("Create file: " + dataFile);

        if (!dataFile.canWrite()) {
          throw new IOException("can not write file: " + dataFile);
        }

        for (int i = 0; i < dataLength; i++) {
          datas[i] = random.nextInt(dataLength);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true))) {
          for (int d : datas) {
            writer.append(String.valueOf(d));
            writer.newLine();
          }
          writer.flush();
        } catch (Exception e) {
          throw new IOException("write data to file: " + dataFile, e);
        }
      }
    } else {
      try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
        String line = reader.readLine();
        int index = 0;
        while (line != null) {
          datas[index++] = Integer.valueOf(line);
          if (index >= dataLength) {
            break;
          }
          line = reader.readLine();
        }
      } catch (Exception e) {
        throw new IOException("read data failed.", e);
      }
    }
  }

  public void betterSort() {
    TotalTimeHelper helper = TotalTimeHelper.of("开始稍好的算法排序。");

    int[] data1 = new int[k];
    System.arraycopy(datas, 0, data1, 0, k);

    for (int i = 0; i < data1.length; i++) {
      for (int j = 0; j < data1.length - 1 - i; j++) {
        if (data1[j] < data1[j + 1]) {
          int temp = data1[j];
          data1[j] = data1[j + 1];
          data1[j + 1] = temp;
        }
      }
    }

    int[] data2 = new int[dataLength - k];
    System.arraycopy(datas, k, data2, 0, dataLength - k);

    for (int d2 : data2) {
      int index = k - 1;
      if (d2 <= data1[index]) {
        continue;
      }

      while (index > 0) {
        if (d2 <= data1[index - 1]) {
          break;
        }
        index--;
      }

      if (index < k - 1) {
        int temp = data1[index];
        data1[index] = d2;
        for (int j = index + 1; j < data1.length; j++) {
          int t = data1[j];
          data1[j] = temp;
          temp = t;
        }
      } else {
        data1[index] = d2;
      }
    }

    System.out.println("find k data: " + data1[k - 1]);
    helper.total();
  }
}
