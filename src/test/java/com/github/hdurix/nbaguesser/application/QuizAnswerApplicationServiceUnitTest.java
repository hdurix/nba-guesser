package com.github.hdurix.nbaguesser.application;

import static com.github.hdurix.nbaguesser.domain.QuizAnswerFixture.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class QuizAnswerApplicationServiceUnitTest {

  @Mock
  private QuizAnswers quizAnswers;

  @InjectMocks
  private QuizAnswerApplicationService quizAnswerApplicationService;

  @Test
  void shouldSaveQuizAnswer() {
    QuizAnswer answer = makeQuizAnswer();

    quizAnswerApplicationService.save(answer);

    verify(quizAnswers).save(answer);
  }
}
