package com.github.hdurix.nbaguesser.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class PlayerFixture {

  public static Player makePlayer() {
    return makePlayer(2);
  }

  public static Player makePlayer(int id) {
    return new Player(id, "Nic", "Claxton");
  }

  public static Collection<Player> makePlayersWithIds(int[] ids) {
    return Arrays.stream(ids).mapToObj(PlayerFixture::makePlayer).toList();
  }

  public static Collection<Player> makeSuggestedPlayers() {
    return makeSuggestedPlayersWithLastId(9);
  }

  public static Collection<Player> makeSuggestedPlayersWithLastId(int id) {
    List<Player> suggestedPlayers = new ArrayList<>(IntStream.range(0, 9).mapToObj(PlayerFixture::makePlayer).toList());
    suggestedPlayers.add(makePlayer(id));
    return suggestedPlayers;
  }

  public static Collection<Player> makeTwentyPlayers() {
    return IntStream.range(0, 20).mapToObj(PlayerFixture::makePlayer).toList();
  }
}
