package com.github.mrzhqiang.section1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RandomData {

  private final int dataLength = 3000_0000;
  private final int[] datas = new int[dataLength];

  private final Random random = new SecureRandom();

  public void write(@Nonnull File file) throws IOException {
    if (!file.exists()) {
      if (file.createNewFile()) {
        System.out.println("Create file: " + file);
      }
    } else {
      boolean b = file.delete() && file.createNewFile();
      if (!b) {
        throw new IOException("delete file and create new file is failed: " + file);
      }
    }

    writeDataTo(file);
  }

  private void writeDataTo(@Nonnull File file) throws IOException {
    if (!file.canWrite()) {
      throw new IOException("can not write file: " + file);
    }

    try (FileWriter writer = new FileWriter(file, true)) {
      for (int d : datas) {
        writer.append(String.valueOf(d)).append("\n");
      }
      writer.flush();
    } catch (Exception e) {
      throw new IOException("write data to file: " + file, e);
    }
  }

  public void generateData() {
    for (int i = 0; i < dataLength; i++) {
      datas[i] = random.nextInt(dataLength);
    }
  }
}
