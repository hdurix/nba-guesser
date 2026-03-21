package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.NbaGuesserApp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuizzGenerator {

  private static final Logger log = LoggerFactory.getLogger(QuizzGenerator.class);

  private final Players players;
  private final Quizzes quizzes;
  private final Randomizer randomizer;

  public QuizzGenerator(Players players, Quizzes quizzes, Randomizer randomizer) {
    this.players = players;
    this.quizzes = quizzes;
    this.randomizer = randomizer;
  }

  public void generate() {
    log.info("generate new quiz");

    Collection<Player> allPlayers = players.listAll();

    log.info(allPlayers.size() + " players found");

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
