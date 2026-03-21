package com.github.hdurix.nbaguesser.domain;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import org.junit.jupiter.api.Test;

@UnitTest
class UserScoreTest {

  @Test
  void shouldNotCreateWithBlankUserName() {
    assertThatThrownBy(() -> new UserScore(" ", 1, 1)).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotCreateWithNullUserName() {
    assertThatThrownBy(() -> new UserScore(null, 1, 1)).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldCalculateAccuracy() {
    UserScore score = new UserScore("Alice", 15, 20);

    assertThat(score.accuracy()).isEqualTo(75);
  }

  @Test
  void shouldReturnZeroAccuracyForNoAnswers() {
    UserScore score = new UserScore("Alice", 0, 0);

    assertThat(score.accuracy()).isEqualTo(0);
  }

  @Test
  void shouldRoundAccuracy() {
    UserScore score = new UserScore("Alice", 1, 3);

    assertThat(score.accuracy()).isEqualTo(33);
  }

  @Test
  void shouldSortByAccuracyDescending() {
    UserScore high = new UserScore("Alice", 8, 10);
    UserScore low = new UserScore("Bob", 5, 10);

    assertThat(high.compareTo(low)).isNegative();
    assertThat(low.compareTo(high)).isPositive();
  }

  @Test
  void shouldSortByTotalAnswersDescendingWhenSameAccuracy() {
    UserScore moreAnswers = new UserScore("Alice", 8, 10);
    UserScore fewerAnswers = new UserScore("Bob", 4, 5);

    assertThat(moreAnswers.compareTo(fewerAnswers)).isNegative();
  }

  @Test
  void shouldBeEqualWhenSameAccuracyAndTotal() {
    UserScore a = new UserScore("Alice", 8, 10);
    UserScore b = new UserScore("Bob", 8, 10);

    assertThat(a.compareTo(b)).isZero();
  }
}
