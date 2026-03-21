package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class StatsGeneratorTest {

  @Mock
  private QuizResults quizResults;

  @Mock
  private QuizAnswers quizAnswers;

  @Test
  void shouldReturnEmptyLeaderboardWhenNoQuizzes() {
    when(quizResults.findByChatId("456")).thenReturn(List.of());

    StatsGenerator generator = new StatsGenerator(quizResults, quizAnswers);
    Leaderboard leaderboard = generator.generateLeaderboard("456");

    assertThat(leaderboard.isEmpty()).isTrue();
  }

  @Test
  void shouldGenerateLeaderboard() {
    QuizResult quiz1 = makeQuizResultBuilder().pollId("poll1").correctOptionIndex(2).build();
    QuizResult quiz2 = makeQuizResultBuilder().pollId("poll2").correctOptionIndex(0).build();
    when(quizResults.findByChatId("456")).thenReturn(List.of(quiz1, quiz2));

    List<QuizAnswer> answers = List.of(
      makeAnswer("poll1", 1L, "Alice", 2),
      makeAnswer("poll1", 2L, "Bob", 1),
      makeAnswer("poll2", 1L, "Alice", 0),
      makeAnswer("poll2", 2L, "Bob", 0)
    );
    when(quizAnswers.findByPollIds(any(Collection.class))).thenReturn(answers);

    StatsGenerator generator = new StatsGenerator(quizResults, quizAnswers);
    Leaderboard leaderboard = generator.generateLeaderboard("456");

    assertThat(leaderboard.scores()).hasSize(2);
    assertThat(leaderboard.scores().get(0).userName()).isEqualTo("Alice");
    assertThat(leaderboard.scores().get(0).correctAnswers()).isEqualTo(2);
    assertThat(leaderboard.scores().get(0).totalAnswers()).isEqualTo(2);
    assertThat(leaderboard.scores().get(1).userName()).isEqualTo("Bob");
    assertThat(leaderboard.scores().get(1).correctAnswers()).isEqualTo(1);
    assertThat(leaderboard.scores().get(1).totalAnswers()).isEqualTo(2);
  }

  private static QuizAnswer makeAnswer(String pollId, long userId, String userName, int optionIndex) {
    return QuizAnswer.builder()
      .pollId(pollId)
      .userId(userId)
      .userName(userName)
      .optionIndex(optionIndex)
      .timestamp(Instant.parse("2026-03-21T08:30:00Z"))
      .build();
  }
}
