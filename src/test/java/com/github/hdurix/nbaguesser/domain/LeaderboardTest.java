package com.github.hdurix.nbaguesser.domain;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import java.util.List;
import org.junit.jupiter.api.Test;

@UnitTest
class LeaderboardTest {

  @Test
  void shouldNotCreateWithNullScores() {
    assertThatThrownBy(() -> new Leaderboard(null)).isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldDetectEmptyLeaderboard() {
    Leaderboard leaderboard = new Leaderboard(List.of());

    assertThat(leaderboard.isEmpty()).isTrue();
  }

  @Test
  void shouldDetectNonEmptyLeaderboard() {
    Leaderboard leaderboard = new Leaderboard(List.of(new UserScore("Alice", 1, 1)));

    assertThat(leaderboard.isEmpty()).isFalse();
  }

  @Test
  void shouldFormatEmptyLeaderboard() {
    Leaderboard leaderboard = new Leaderboard(List.of());

    assertThat(leaderboard.format()).contains("Aucune r\u00E9ponse pour le moment.");
  }

  @Test
  void shouldFormatLeaderboardWithMedals() {
    List<UserScore> scores = List.of(
      new UserScore("Alice", 15, 20),
      new UserScore("Bob", 17, 25),
      new UserScore("Charlie", 12, 20),
      new UserScore("Dave", 11, 20)
    );
    Leaderboard leaderboard = new Leaderboard(scores);

    String formatted = leaderboard.format();

    assertThat(formatted).contains("\uD83C\uDFC0 NBA Guesser \u2014 Classement");
    assertThat(formatted).contains("1. \uD83E\uDD47 Alice \u2014 75% (15/20)");
    assertThat(formatted).contains("2. \uD83E\uDD48 Bob \u2014 68% (17/25)");
    assertThat(formatted).contains("3. \uD83E\uDD49 Charlie \u2014 60% (12/20)");
    assertThat(formatted).contains("4. Dave \u2014 55% (11/20)");
  }
}
