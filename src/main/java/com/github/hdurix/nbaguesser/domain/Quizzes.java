package com.github.hdurix.nbaguesser.domain;

import java.util.Collection;

public interface Quizzes {
  QuizResult send(Collection<Player> suggestedPlayers, Player correctPlayer);
}
