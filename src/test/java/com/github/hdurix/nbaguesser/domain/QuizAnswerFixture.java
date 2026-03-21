package com.github.hdurix.nbaguesser.domain;

import java.time.Instant;

public class QuizAnswerFixture {

  public static QuizAnswer.QuizAnswerBuilder makeQuizAnswerBuilder() {
    return QuizAnswer
      .builder()
      .pollId("5678")
      .userId(123456789L)
      .userName("John")
      .optionIndex(2)
      .timestamp(Instant.parse("2026-03-21T08:30:00Z"));
  }

  public static QuizAnswer makeQuizAnswer() {
    return makeQuizAnswerBuilder().build();
  }
}
