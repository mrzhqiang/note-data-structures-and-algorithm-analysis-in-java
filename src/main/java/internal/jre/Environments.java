package internal.jre;

public enum Environments {
  ; // no instance

  /**
   * Check debug mode.
   * <p>
   * The method works only in IntelliJ IDEA.
   *
   * @return true if the current environment is in debug mode.
   */
  public static boolean debugForIDEA() {
    return Boolean.parseBoolean(System.getProperty("intellij.debug.agent", "false"));
  }

}
