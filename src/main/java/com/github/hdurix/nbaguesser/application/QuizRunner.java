package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.common.domain.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "application.runner.enabled", havingValue = "true", matchIfMissing = true)
@Generated(reason = "Not testing wiring")
class QuizRunner implements CommandLineRunner {

  private final PlayersApplicationService players;

  QuizRunner(PlayersApplicationService players) {
    this.players = players;
  }

  @Override
  public void run(String... args) {
    players.generateQuiz();
  }
}
