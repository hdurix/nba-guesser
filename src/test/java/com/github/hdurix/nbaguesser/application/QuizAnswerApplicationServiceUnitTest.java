package com.github.hdurix.nbaguesser.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    quizAnswerApplicationService.save("poll123", 42L, "John", 2);

    ArgumentCaptor<QuizAnswer> captor = ArgumentCaptor.forClass(QuizAnswer.class);
    verify(quizAnswers).save(captor.capture());
    QuizAnswer saved = captor.getValue();
    assertThat(saved.pollId()).isEqualTo("poll123");
    assertThat(saved.userId()).isEqualTo(42L);
    assertThat(saved.userName()).isEqualTo("John");
    assertThat(saved.optionIndex()).isEqualTo(2);
    assertThat(saved.timestamp()).isNotNull();
  }
}
