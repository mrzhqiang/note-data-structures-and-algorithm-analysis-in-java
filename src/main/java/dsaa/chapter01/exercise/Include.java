package dsaa.chapter01.exercise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 习题1.4
 * 模仿 C语言 的 include 语句，并允许文件嵌套 include 语句。
 *
 * 注意，导入时请不要包含自身。
 */
public class Include {
  public static void run() {
    File file = new File("..\\include\\1.dat");
    parseFile(file);
  }

  private static void parseFile(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line = reader.readLine();
      while (line != null) {
        if (line.startsWith("#include ")) {
          String fileName = line.replace("#include ", "");
          if (fileName.equals(file.getName())) {
            System.out.println("can't not include self.");
            line = reader.readLine();
            continue;
          }
          if (!fileName.contains("\\") && !fileName.contains("/")) {
            parseFile(new File("..\\include\\" + fileName));
          } else {
            parseFile(new File("..\\" + fileName));
          }
        } else {
          System.out.println(line);
        }
        line = reader.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
