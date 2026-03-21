package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class PlayersApplicationServiceIntTest {

  @Autowired
  private PlayersApplicationService service;

  @Test
  void shouldGenerateQuizz() {
    service.generateQuizz();
  }
}
