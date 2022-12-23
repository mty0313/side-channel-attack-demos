import common.EqualFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimingAttack {
  // hippopotamus
  public static final String ANSWER = "apple";
  public static final int ANSWER_LENGTH = ANSWER.length();
  public static final int ROUND = 4; // 算法内算分用的轮数
  public static final int ATTEMPTS = 100000; // 程序自动尝试的次数
  public static final String[] CANDIDATES = new String[] {"a", "b", "c", "d", "e", "f", "g", "h",
      "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

  public static void attackDemo1() throws InterruptedException {
    String[] attackString = new String[ANSWER_LENGTH];
    for (int i = 0; i < ANSWER_LENGTH; i++) {
      attackString[i] = CANDIDATES[0];
    }
    for (int position = 0; position < ANSWER_LENGTH - 1; position++) {
      List<CharDuration> scores = new ArrayList<>();
      for (String candidate : CANDIDATES) {
        attackString[position] = candidate;
        long start = System.currentTimeMillis();
        EqualFunction.slowerInsecureEqual(ANSWER, buildStringFromArray(attackString));
        long duration = System.currentTimeMillis() - start;
        scores.add(new CharDuration(candidate, duration));
      }
      String mostLikelyCharAtPosition = mostLikelyChar(scores);
      attackString[position] = mostLikelyCharAtPosition;
      System.out.println("最后可能在位置 " + position + " 的字符：" + mostLikelyChar(scores));
    }
    for (String candidate : CANDIDATES) {
      int lastPos = ANSWER_LENGTH - 1;
      attackString[lastPos] = candidate;
      if (EqualFunction.slowerInsecureEqual(ANSWER, buildStringFromArray(attackString))) {
        attackString[lastPos] = candidate;
        System.out.println("最后一位 " + lastPos + " 的字符确认为：" + candidate);
        break;
      }
    }
    System.out.println("结果为：" + buildStringFromArray(attackString));
  }

  public static boolean attackDemo2(boolean debug) {
    String[] attackStringArray = new String[ANSWER_LENGTH];
    for (int i = 0; i < ANSWER_LENGTH; i++) {
      attackStringArray[i] = CANDIDATES[0];
    }
    for (int position = 0; position < ANSWER_LENGTH - 1; position++) {
      Map<String, List<Integer>> roundScores = new HashMap<>();
      for (String charValue : CANDIDATES) {
        roundScores.put(charValue, new ArrayList<>());
      }
      for (int r = 0; r < ROUND; r++) {
        List<CharDuration> durations = new ArrayList<>();
        for (String candidate : CANDIDATES) {
          attackStringArray[position] = candidate;
          String attackString = buildStringFromArray(attackStringArray);
          long start = System.nanoTime();
          EqualFunction.insecureEqual(ANSWER, attackString);
          long duration = System.nanoTime() - start;
          durations.add(new CharDuration(candidate, duration));
        }
        scores(roundScores, durations);
      }
      Map<String, Double> thisPosScore = new HashMap<>();
      for (Map.Entry<String, List<Integer>> perCharRoundScores : roundScores.entrySet()) {
        long sum = 0L;
        for (Integer score : perCharRoundScores.getValue()) {
          sum += score;
        }
        double avgScore = 1.0 * sum / ROUND;
        thisPosScore.put(perCharRoundScores.getKey(), avgScore);
      }
      double maxPosScore = 0.0;
      String mostLikelyCharAtPos = null;
      for (Map.Entry<String, Double> entry : thisPosScore.entrySet()) {
        if (entry.getValue() > maxPosScore) {
          maxPosScore = entry.getValue();
          mostLikelyCharAtPos = entry.getKey();
        }
      }
      attackStringArray[position] = mostLikelyCharAtPos;
      if (debug) {
        System.out.println("位置 " + position + " 的得分情况：" + thisPosScore);
        System.out.println("最有可能在位置 " + position + " 的字符：" + mostLikelyCharAtPos);
        System.out.println("当前用于尝试的字符串更新为：" + Arrays.toString(attackStringArray));
      }
    }
    // 最后一位
    for (String candidate : CANDIDATES) {
      int lastPos = ANSWER_LENGTH - 1;
      attackStringArray[lastPos] = candidate;
      if (EqualFunction.insecureEqual(ANSWER, buildStringFromArray(attackStringArray))) {
        attackStringArray[lastPos] = candidate;
        if (debug) {
          System.out.println("最后一位 " + lastPos + " 的字符确认为：" + candidate);
        }
        System.out.println("结果为：" + buildStringFromArray(attackStringArray));
        return true;
      }
    }
    return false;
  }

  private static String buildStringFromArray(String[] array) {
    StringBuilder result = new StringBuilder();
    for (String s : array) {
      result.append(s);
    }
    return result.toString();
  }

  private static String mostLikelyChar(List<CharDuration> durations) {
    List<Double> scoreValues = durations.stream().map(CharDuration::getDuration).collect(Collectors.toList());
    double medium = scoreValues.get(durations.size() / 2);
    CharDuration result = new CharDuration();
    double max = 0.0;
    for (CharDuration charDuration : durations) {
      double score = charDuration.getDuration() - medium;
      if (max < score) {
        max = score;
        result.setCharValue(charDuration.charValue);
        result.setDuration(max);
      }
    }
    return result.getCharValue();
  }

  /**
   *
   *  {"a", [2, 2], "b", [1, 1] ...}
   */
  private static void scores(Map<String, List<Integer>> roundScore, List<CharDuration> durations) {
    durations.sort(Comparator.comparing(CharDuration::getDuration));
    for (int i = 1; i <= durations.size(); i++) {
      roundScore.get(durations.get(i - 1).getCharValue()).add(i);
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class CharDuration {
    String charValue;
    double duration;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class CharScore {
    String charValue;
    List<Integer> scores;
  }

  public static void main(String[] args) throws InterruptedException {
//    attackDemo1();
    int attemptsCount = 0;
    while (attemptsCount++ < ATTEMPTS) {
      System.out.println("第 " + attemptsCount + " 次尝试：");
      if (attackDemo2(false)) {
        System.out.println("调用equals方法 " + attemptsCount * ROUND + " 次后找到答案");
        return;
      }
    }
    System.out.println("调用equals方法 " + ATTEMPTS * ROUND + " 次后仍然没有找到答案");
  }

}
