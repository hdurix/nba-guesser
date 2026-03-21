package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerApplicationService {

  private final QuizAnswers quizAnswers;

  public QuizAnswerApplicationService(QuizAnswers quizAnswers) {
    this.quizAnswers = quizAnswers;
  }

  public void save(QuizAnswer answer) {
    quizAnswers.save(answer);
  }
}
