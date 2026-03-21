package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
class StatsApplicationServiceIntTest {

  @Autowired
  private StatsApplicationService service;

  @Value("${telegram.chat_id}")
  private String chatId;

  @Test
  void shouldSendStats() {
    service.sendStats(chatId);
  }
}
