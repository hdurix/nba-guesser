package com.github.hdurix.nbaguesser.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuizzGenerator {

  private final Players players;
  private final Quizzes quizzes;
  private final Randomizer randomizer;

  public QuizzGenerator(Players players, Quizzes quizzes, Randomizer randomizer) {
    this.players = players;
    this.quizzes = quizzes;
    this.randomizer = randomizer;
  }

  public void generate() {
    Collection<Player> allPlayers = players.listAll();
    int[] randomTenIndexes = randomizer.tenOutOf(allPlayers.size());
    Collection<Player> suggestedPlayers = mapSuggestedPlayers(allPlayers, randomTenIndexes);

    this.quizzes.send(suggestedPlayers, pickCorrectPlayer(suggestedPlayers));
  }

  private static Collection<Player> mapSuggestedPlayers(Collection<Player> allPlayers, int[] indexes) {
    List<Player> playerList = allPlayers.stream().toList();

    return Arrays.stream(indexes).mapToObj(playerList::get).toList();
  }

  private Player pickCorrectPlayer(Collection<Player> suggestedPlayers) {
    return randomizer.oneOf(suggestedPlayers);
  }
}
