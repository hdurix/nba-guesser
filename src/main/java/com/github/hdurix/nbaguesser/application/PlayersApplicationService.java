package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.domain.Players;
import com.github.hdurix.nbaguesser.domain.QuizGenerator;
import com.github.hdurix.nbaguesser.domain.QuizResults;
import com.github.hdurix.nbaguesser.domain.Quizzes;
import com.github.hdurix.nbaguesser.domain.Randomizer;
import org.springframework.stereotype.Service;

@Service
public class PlayersApplicationService {

  private final QuizGenerator quizGenerator;

  public PlayersApplicationService(Players players, Quizzes quizzes, QuizResults quizResults) {
    quizGenerator = new QuizGenerator(players, quizzes, quizResults, new Randomizer());
  }

  public void generateQuiz() {
    quizGenerator.generate();
  }
}
