package com.github.hdurix.nbaguesser.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuizGenerator {

  private static final Logger log = LoggerFactory.getLogger(QuizGenerator.class);

  private final Players players;
  private final Quizzes quizzes;
  private final QuizResults quizResults;
  private final Randomizer randomizer;

  public QuizGenerator(Players players, Quizzes quizzes, QuizResults quizResults, Randomizer randomizer) {
    this.players = players;
    this.quizzes = quizzes;
    this.quizResults = quizResults;
    this.randomizer = randomizer;
  }

  public void generate() {
    log.info("generate new quiz");

    Collection<Player> allPlayers = players.listAll();

    log.info("{} players found", allPlayers.size());

    int[] randomTenIndexes = randomizer.tenOutOf(allPlayers.size());
    Collection<Player> suggestedPlayers = mapSuggestedPlayers(allPlayers, randomTenIndexes);

    QuizResult result = this.quizzes.send(suggestedPlayers, pickCorrectPlayer(suggestedPlayers));
    this.quizResults.save(result);
  }

  private static Collection<Player> mapSuggestedPlayers(Collection<Player> allPlayers, int[] indexes) {
    List<Player> playerList = allPlayers.stream().toList();

    return Arrays.stream(indexes).mapToObj(playerList::get).toList();
  }

  private Player pickCorrectPlayer(Collection<Player> suggestedPlayers) {
    return randomizer.oneOf(suggestedPlayers);
  }
}
