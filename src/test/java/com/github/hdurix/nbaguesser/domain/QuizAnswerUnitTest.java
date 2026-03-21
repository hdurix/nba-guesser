package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.QuizAnswerFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

@UnitTest
class QuizAnswerUnitTest {

  @Test
  void shouldNotBuildWithBlankPollId() {
    assertThatThrownBy(() -> makeQuizAnswerBuilder().pollId(" ").build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotBuildWithBlankUserName() {
    assertThatThrownBy(() -> makeQuizAnswerBuilder().userName(" ").build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotBuildWithNullTimestamp() {
    assertThatThrownBy(() -> makeQuizAnswerBuilder().timestamp(null).build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldBuild() {
    QuizAnswer answer = makeQuizAnswer();

    assertThat(answer.pollId()).isEqualTo("5678");
    assertThat(answer.userId()).isEqualTo(123456789L);
    assertThat(answer.userName()).isEqualTo("John");
    assertThat(answer.optionIndex()).isEqualTo(2);
    assertThat(answer.timestamp()).isEqualTo(Instant.parse("2026-03-21T08:30:00Z"));
  }
}
