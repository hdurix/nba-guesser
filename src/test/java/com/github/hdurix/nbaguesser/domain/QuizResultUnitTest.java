package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import org.junit.jupiter.api.Test;

@UnitTest
class QuizResultUnitTest {

  @Test
  void shouldNotBuildWithBlankPollId() {
    assertThatThrownBy(() -> makeQuizResultBuilder().pollId(" ").build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotBuildWithNullDate() {
    assertThatThrownBy(() -> makeQuizResultBuilder().date(null).build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotBuildWithEmptyOptions() {
    assertThatThrownBy(() -> makeQuizResultBuilder().options(java.util.List.of()).build()).isExactlyInstanceOf(
      MissingMandatoryValueException.class
    );
  }

  @Test
  void shouldNotBuildWithBlankCorrectAnswer() {
    assertThatThrownBy(() -> makeQuizResultBuilder().correctAnswer(" ").build()).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldBuild() {
    QuizResult result = makeQuizResult();

    assertThat(result.pollId()).isEqualTo("5678");
    assertThat(result.correctAnswer()).isEqualTo("Nic Claxton");
  }
}
