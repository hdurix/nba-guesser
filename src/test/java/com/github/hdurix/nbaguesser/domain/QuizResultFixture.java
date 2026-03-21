package com.github.hdurix.nbaguesser.domain;

import java.time.LocalDate;
import java.util.List;

public class QuizResultFixture {

  public static QuizResult makeQuizResult() {
    return new QuizResult(
      "5678",
      LocalDate.of(2026, 3, 21),
      List.of(
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton",
        "Nic Claxton"
      ),
      2,
      "Nic Claxton",
      123L
    );
  }
}
