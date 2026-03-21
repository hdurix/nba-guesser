package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerApplicationService {

  private final QuizAnswers quizAnswers;

  public QuizAnswerApplicationService(QuizAnswers quizAnswers) {
    this.quizAnswers = quizAnswers;
  }

  public void save(String pollId, long userId, String userName, int optionIndex) {
    QuizAnswer answer = QuizAnswer.builder()
      .pollId(pollId)
      .userId(userId)
      .userName(userName)
      .optionIndex(optionIndex)
      .timestamp(Instant.now())
      .build();

    quizAnswers.save(answer);
  }
}
