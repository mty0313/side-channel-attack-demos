package common;

public class EqualFunction {
  public static boolean insecureEqual(String a, String b) {
    if (a.length() != b.length()) {
      return false;
    }
    for (int i = 0; i < a.length(); i++) {
      if (a.charAt(i) != b.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param a bird
   * @param b aaaa
   * @return
   * @throws InterruptedException
   */
  public static boolean slowerInsecureEqual(String a, String b) throws InterruptedException {
    if (a.length() != b.length()) {
      return false;
    }
    for (int i = 0; i < a.length(); i++) {
      Thread.sleep(100);
      if (a.charAt(i) != b.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
