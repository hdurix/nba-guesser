package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

@UnitTest
class QuizResultUnitTest {

  @Test
  void shouldNotCreateWithBlankPollId() {
    assertThatThrownBy(() -> new QuizResult(" ", LocalDate.now(), List.of("A"), 0, "A", 1L)).isExactlyInstanceOf(
      MissingMandatoryValueException.class
    );
  }

  @Test
  void shouldNotCreateWithNullDate() {
    assertThatThrownBy(() -> new QuizResult("id", null, List.of("A"), 0, "A", 1L)).isExactlyInstanceOf(
      MissingMandatoryValueException.class
    );
  }

  @Test
  void shouldNotCreateWithEmptyOptions() {
    assertThatThrownBy(() -> new QuizResult("id", LocalDate.now(), List.of(), 0, "A", 1L)).isExactlyInstanceOf(
      MissingMandatoryValueException.class
    );
  }

  @Test
  void shouldNotCreateWithBlankCorrectAnswer() {
    assertThatThrownBy(() -> new QuizResult("id", LocalDate.now(), List.of("A"), 0, " ", 1L)).isExactlyInstanceOf(
      MissingMandatoryValueException.class
    );
  }

  @Test
  void shouldCreateValidQuizResult() {
    QuizResult result = makeQuizResult();

    assertThat(result.pollId()).isEqualTo("5678");
    assertThat(result.correctAnswer()).isEqualTo("Nic Claxton");
  }
}
