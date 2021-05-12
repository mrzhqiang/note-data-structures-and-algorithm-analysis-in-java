package internal.jre;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class EnvironmentsTest {

  @Test
  public void debug() {
    String property = System.getProperty("intellij.debug.agent");
    boolean debug = Environments.debugForIDEA();
    log.debug("property={}, debug={}", property, debug);
    assertEquals(Boolean.parseBoolean(property), debug);
  }
}