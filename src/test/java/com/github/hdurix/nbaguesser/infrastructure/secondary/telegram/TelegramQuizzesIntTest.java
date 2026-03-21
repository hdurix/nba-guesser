package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;

import com.github.hdurix.nbaguesser.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("Hits real Telegram API - for local development only")
@IntegrationTest
class TelegramQuizzesIntTest {

  @Autowired
  private TelegramQuizzes telegramQuizzes;

  @Test
  void shouldSendTelegramQuizz() {
    telegramQuizzes.send(makeSuggestedPlayersWithLastId(1628966), makePlayer(1628966));
  }
}
