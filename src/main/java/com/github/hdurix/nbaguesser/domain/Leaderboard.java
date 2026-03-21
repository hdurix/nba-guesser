package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.util.List;

public record Leaderboard(List<UserScore> scores) {
  public Leaderboard {
    Assert.notNull("scores", scores);
  }

  public boolean isEmpty() {
    return scores.isEmpty();
  }

  public String format() {
    if (scores.isEmpty()) {
      return "\uD83C\uDFC0 NBA Guesser \u2014 Classement\n\nAucune r\u00E9ponse pour le moment.";
    }

    StringBuilder sb = new StringBuilder("\uD83C\uDFC0 NBA Guesser \u2014 Classement\n");
    for (int i = 0; i < scores.size(); i++) {
      UserScore score = scores.get(i);
      int rank = i + 1;
      sb.append("\n").append(rank).append(". ").append(medal(rank)).append(score.userName());
      sb
        .append(" \u2014 ")
        .append(score.accuracy())
        .append("% (")
        .append(score.correctAnswers())
        .append("/")
        .append(score.totalAnswers())
        .append(")");
    }
    return sb.toString();
  }

  private static String medal(int rank) {
    return switch (rank) {
      case 1 -> "\uD83E\uDD47 ";
      case 2 -> "\uD83E\uDD48 ";
      case 3 -> "\uD83E\uDD49 ";
      default -> "";
    };
  }
}
