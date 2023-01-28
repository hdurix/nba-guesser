package com.github.hdurix.nbaguesser.domain;

import java.util.Collection;

public interface Quizzes {
  void send(Collection<Player> suggestedPlayers, Player correctPlayer);
}
