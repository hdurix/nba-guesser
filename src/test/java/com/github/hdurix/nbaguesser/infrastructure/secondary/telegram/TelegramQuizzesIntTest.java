package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.IntegrationTest;
import com.github.hdurix.nbaguesser.domain.QuizResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TelegramQuizzesIntTest {

  @Autowired
  private TelegramQuizzes telegramQuizzes;

  @Test
  void shouldSendTelegramQuiz() {
    QuizResult result = telegramQuizzes.send(makeSuggestedPlayersWithLastId(1628966), makePlayer(1628966));

    assertThat(result.pollId()).isNotBlank();
    assertThat(result.messageId()).isPositive();
  }
}
