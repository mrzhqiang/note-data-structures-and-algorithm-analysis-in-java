package dsaa.chapter01.section04;

import javax.annotation.Nullable;

public class MemoryCell {
  @Nullable
  private Object storedValue;

  @Nullable
  public Object read() {
    return storedValue;
  }

  public void write(@Nullable Object value) {
    storedValue = value;
  }
}
