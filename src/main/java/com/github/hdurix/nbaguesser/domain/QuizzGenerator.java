package com.github.hdurix.nbaguesser.domain;

import java.util.ArrayList;
import java.util.Arrays;
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
    List<Player> allPlayers = players.listAll().stream().toList();
    int[] randomIndexes = randomizer.tenOutOf(allPlayers.size());
    ArrayList<Player> playerList = new ArrayList<>();
    Arrays.stream(randomIndexes).forEach(i -> playerList.add(allPlayers.get(i)));

    int correctId = randomizer.oneOf(randomIndexes);

    this.quizzes.send(playerList, playerList.stream().filter(player -> player.id() == correctId).findFirst().orElseThrow());
  }
}
