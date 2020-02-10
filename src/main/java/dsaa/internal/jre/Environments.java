package dsaa.internal.jre;

public enum Environments {
  ;

  /**
   * Check debug mode.
   * <p>
   * The method works only in IntelliJ IDEA.
   *
   * @return true if the current environment is in debug mode.
   */
  public static boolean debug() {
    return Boolean.parseBoolean(System.getProperty("intellij.debug.agent", "false"));
  }
}
