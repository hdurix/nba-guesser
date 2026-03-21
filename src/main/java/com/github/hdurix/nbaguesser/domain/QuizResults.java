package com.github.hdurix.nbaguesser.domain;

import java.util.List;

public interface QuizResults {
  void save(QuizResult result);

  List<QuizResult> findByChatId(String chatId);
}
