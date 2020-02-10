package dsaa.internal.jre;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public enum StackTraces {
  ;

  public static String ofCurrent() {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
      printWriter.println(element);
    }
    return writer.toString();
  }

  public static String of(Throwable e) {
    if (Environments.debug()) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
    }
    return Objects.nonNull(e) ? e.getMessage() : "";
  }
}
