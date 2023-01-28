package com.github.hdurix.nbaguesser.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class PlayerFixture {
  public static Player makePlayer() {
    return makePlayer(2);
  }

  public static Player makePlayer(int id) {
    return new Player(id, "Nicolas", "Claxton");
  }

  public static Collection<Player> makePlayersWithIds(int[] ids) {
    return Arrays.stream(ids).mapToObj(PlayerFixture::makePlayer).toList();
  }

  public static Collection<Player> makeTwentyPlayers() {
    return IntStream.range(0, 20).mapToObj(PlayerFixture::makePlayer).toList();
  }
}
