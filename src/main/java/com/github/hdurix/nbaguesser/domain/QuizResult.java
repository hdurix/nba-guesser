package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.time.LocalDate;
import java.util.List;

public record QuizResult(
  String pollId,
  LocalDate date,
  List<String> options,
  int correctOptionIndex,
  String correctAnswer,
  long messageId
) {
  public QuizResult {
    Assert.notBlank("pollId", pollId);
    Assert.notNull("date", date);
    Assert.notEmpty("options", options);
    Assert.notBlank("correctAnswer", correctAnswer);
  }
}
