import common.EqualFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimingAttack {
  public static final String ANSWER = "bird";
  public static final int ANSWER_LENGTH = 4;
  public static final int ROUND = 900_0000;
  public static final String[] CANDIDATES = new String[] {"a", "b", "c", "d", "e", "f", "g", "h",
      "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

  public static void attackDemo1() throws InterruptedException {
    String[] attackString = new String[]{"a", "a", "a", "a"};
    for (int pos = 0; pos < ANSWER_LENGTH - 1; pos++) {
      List<CharScore> scores = new ArrayList<>();
      for (String candidate : CANDIDATES) {
        attackString[pos] = candidate;
        long start = System.currentTimeMillis();
        EqualFunction.slowerInsecureEqual(ANSWER, buildStringFromArray(attackString));
        long duration = System.currentTimeMillis() - start;
        scores.add(new CharScore(candidate, duration));
      }
      String mostLikelyCharAtPosition = mostLikelyChar(scores);
      attackString[pos] = mostLikelyCharAtPosition;
      System.out.println("mostLikelyChar(scores) at position " + pos + " = " + mostLikelyChar(scores));
    }
    for (String candidate : CANDIDATES) {
      int lastPos = ANSWER_LENGTH - 1;
      attackString[lastPos] = candidate;
      if (EqualFunction.slowerInsecureEqual(ANSWER, buildStringFromArray(attackString))) {
        attackString[lastPos] = candidate;
        System.out.println("mostLikelyChar(scores) at position " + lastPos + " = " + candidate);
        break;
      }
    }
    System.out.println("ANSWER = " + buildStringFromArray(attackString));
  }

  public static void attackDemo2() {
    String[] attackString = new String[]{"a", "a", "a", "a"};
    for (int pos = 0; pos < ANSWER_LENGTH; pos++) {
      List<CharScore> scores = new ArrayList<>();
      for (String candidate : CANDIDATES) {
        attackString[pos] = candidate;
        long start = System.nanoTime();
        for (int i = 0; i < ROUND; i++) {
          EqualFunction.insecureEqual(ANSWER, buildStringFromArray(attackString));
        }
        long duration = System.nanoTime() - start;
        scores.add(new CharScore(candidate, duration));
      }
      String mostLikelyCharAtPosition = mostLikelyChar(scores);
      System.out.println("mostLikelyCharAtPosition " + pos + " = " + mostLikelyCharAtPosition);
      attackString[pos] = mostLikelyCharAtPosition;
    }
    // 最后一位
    for (String candidate : CANDIDATES) {
      int lastPos = ANSWER_LENGTH - 1;
      attackString[lastPos] = candidate;
      if (EqualFunction.insecureEqual(ANSWER, buildStringFromArray(attackString))) {
        attackString[lastPos] = candidate;
        System.out.println("final check: last position: " + lastPos + " = " + candidate);
        break;
      }
    }
    System.out.println("ANSWER = " + buildStringFromArray(attackString));
  }

  private static String buildStringFromArray(String[] array) {
    StringBuilder result = new StringBuilder();
    for (String s : array) {
      result.append(s);
    }
    return result.toString();
  }

  private static String mostLikelyChar(List<CharScore> scores) {
    List<Double> scoreValues = scores.stream().map(CharScore::getScore).collect(Collectors.toList());
    double medium = scoreValues.get(scores.size() / 2);
    CharScore result = new CharScore();
    double max = 0.0;
    for (CharScore charScore : scores) {
      double score = charScore.getScore() - medium;
      if (max < score) {
        max = score;
        result.setCharValue(charScore.charValue);
        result.setScore(max);
      }
    }
    return result.getCharValue();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class CharScore {
    String charValue;
    double score;
  }

  public static void main(String[] args) throws InterruptedException {
    attackDemo2();
  }

}
