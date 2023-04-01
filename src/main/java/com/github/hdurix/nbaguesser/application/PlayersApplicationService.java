package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.domain.Players;
import com.github.hdurix.nbaguesser.domain.QuizzGenerator;
import com.github.hdurix.nbaguesser.domain.Quizzes;
import com.github.hdurix.nbaguesser.domain.Randomizer;
import org.springframework.stereotype.Service;

@Service
public class PlayersApplicationService {

  private final QuizzGenerator quizzGenerator;

  public PlayersApplicationService(Players players, Quizzes quizzes) {
    quizzGenerator = new QuizzGenerator(players, quizzes, new Randomizer());
  }

  public void generateQuizz() {
    quizzGenerator.generate();
  }
}
