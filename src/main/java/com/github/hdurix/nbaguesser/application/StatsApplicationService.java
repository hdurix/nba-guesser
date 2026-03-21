package com.github.hdurix.nbaguesser.application;

import com.github.hdurix.nbaguesser.domain.Leaderboard;
import com.github.hdurix.nbaguesser.domain.Messages;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import com.github.hdurix.nbaguesser.domain.QuizResults;
import com.github.hdurix.nbaguesser.domain.StatsGenerator;
import org.springframework.stereotype.Service;

@Service
public class StatsApplicationService {

  private final StatsGenerator statsGenerator;
  private final Messages messages;

  public StatsApplicationService(QuizResults quizResults, QuizAnswers quizAnswers, Messages messages) {
    this.statsGenerator = new StatsGenerator(quizResults, quizAnswers);
    this.messages = messages;
  }

  public void sendStats(String chatId) {
    Leaderboard leaderboard = statsGenerator.generateLeaderboard(chatId);
    messages.send(chatId, leaderboard.format());
  }
}
