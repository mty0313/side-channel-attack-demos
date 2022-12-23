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

  public static boolean safeEqual(String a, String b) {
    if (a.length() != b.length()) {
      return false;
    }
    int equal = 0;
    for (int i = 0; i < a.length(); i++) {
      equal |= a.charAt(i) ^ b.charAt(i);
    }
    return equal == 0;
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
      Thread.sleep(50);
      if (a.charAt(i) != b.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {
    System.out.println("0^0 = " + (0 ^ 0));
    System.out.println("1^0 = " + (1 ^ 0));
    System.out.println("0^1 = " + (0 ^ 1));
    System.out.println("1^1 = " + (1 ^ 1));
    System.out.println("0|0 = " + (0 | 0));
    System.out.println("0|1 = " + (0 | 1));
    System.out.println("1|0 = " + (1 | 0));
    System.out.println("1|1 = " + (1 | 1));
  }
}
