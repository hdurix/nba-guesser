package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;

public record UserScore(String userName, int correctAnswers, int totalAnswers) implements Comparable<UserScore> {
  public UserScore {
    Assert.notBlank("userName", userName);
  }

  public int accuracy() {
    if (totalAnswers == 0) {
      return 0;
    }
    return (int) Math.round((correctAnswers * 100.0) / totalAnswers);
  }

  @Override
  public int compareTo(UserScore other) {
    int accuracyCompare = Integer.compare(other.accuracy(), this.accuracy());
    if (accuracyCompare != 0) {
      return accuracyCompare;
    }
    return Integer.compare(other.totalAnswers(), this.totalAnswers());
  }
}
