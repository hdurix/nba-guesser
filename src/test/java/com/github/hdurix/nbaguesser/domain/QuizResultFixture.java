package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;

import java.time.LocalDate;
import java.util.List;

public class QuizResultFixture {

  public static QuizResult.QuizResultBuilder makeQuizResultBuilder() {
    List<String> options = makeSuggestedPlayers().stream().map(Player::fullName).toList();

    return QuizResult
      .builder()
      .pollId("5678")
      .chatId("456")
      .date(LocalDate.of(2026, 3, 21))
      .options(options)
      .correctOptionIndex(2)
      .correctAnswer("Nic Claxton")
      .messageId(123L);
  }

  public static QuizResult makeQuizResult() {
    return makeQuizResultBuilder().build();
  }
}
