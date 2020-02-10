package dsaa.internal.jre;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class StackTracesTest {

  @Test
  public void ofCurrent() {
    String firstTrace = StackTraces.ofCurrent();
    String secondTrace = StackTraces.ofCurrent();
    assertNotEquals(firstTrace, secondTrace);
  }

  @Test
  public void of() {
    String runtime = StackTraces.of(new RuntimeException("runtime"));
    String nullPointer = StackTraces.of(new NullPointerException("null"));
    assertNotEquals(runtime, nullPointer);
  }
}