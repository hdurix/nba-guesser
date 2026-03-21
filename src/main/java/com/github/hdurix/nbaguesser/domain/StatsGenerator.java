package com.github.hdurix.nbaguesser.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsGenerator {

  private final QuizResults quizResults;
  private final QuizAnswers quizAnswers;

  public StatsGenerator(QuizResults quizResults, QuizAnswers quizAnswers) {
    this.quizResults = quizResults;
    this.quizAnswers = quizAnswers;
  }

  public Leaderboard generateLeaderboard(String chatId) {
    List<QuizResult> quizzes = quizResults.findByChatId(chatId);

    if (quizzes.isEmpty()) {
      return new Leaderboard(List.of());
    }

    Map<String, Integer> correctOptionByPollId = quizzes
      .stream()
      .collect(Collectors.toMap(QuizResult::pollId, QuizResult::correctOptionIndex));

    Collection<String> pollIds = correctOptionByPollId.keySet();
    List<QuizAnswer> answers = quizAnswers.findByPollIds(pollIds);

    Map<Long, UserScoreAccumulator> accumulators = new HashMap<>();
    for (QuizAnswer answer : answers) {
      UserScoreAccumulator accumulator = accumulators.computeIfAbsent(answer.userId(), _ -> new UserScoreAccumulator(answer.userName()));
      Integer correctOption = correctOptionByPollId.get(answer.pollId());
      accumulator.addAnswer(answer.optionIndex() == correctOption);
    }

    List<UserScore> scores = accumulators.values().stream().map(UserScoreAccumulator::toUserScore).sorted().toList();

    return new Leaderboard(scores);
  }

  private static class UserScoreAccumulator {

    private final String userName;
    private int correct;
    private int total;

    UserScoreAccumulator(String userName) {
      this.userName = userName;
    }

    void addAnswer(boolean isCorrect) {
      total++;
      if (isCorrect) {
        correct++;
      }
    }

    UserScore toUserScore() {
      return new UserScore(userName, correct, total);
    }
  }
}
