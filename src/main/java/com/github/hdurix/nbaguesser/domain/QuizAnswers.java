package com.github.hdurix.nbaguesser.domain;

import java.util.Collection;
import java.util.List;

public interface QuizAnswers {
  void save(QuizAnswer answer);

  List<QuizAnswer> findByPollIds(Collection<String> pollIds);
}
