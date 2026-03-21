package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class ApiResultSet {

  private static final int PLAYER_ID_INDEX = 0;
  private static final int LAST_NAME_INDEX = 1;
  private static final int FIRST_NAME_INDEX = 2;
  private static final int ROSTER_STATUS_INDEX = 19;

  public static final String PLAYING_ROSTER_STATUS = "1.0";

  @JsonProperty
  private Collection<List<String>> rowSet;

  public Collection<Player> toPlayers() {
    return rowSet.stream().filter(isActivePlayer()).map(toPlayer()).toList();
  }

  private static Predicate<List<String>> isActivePlayer() {
    return row -> {
      String rosterStatus = row.get(ROSTER_STATUS_INDEX);

      return PLAYING_ROSTER_STATUS.equals(rosterStatus);
    };
  }

  private static Function<List<String>, Player> toPlayer() {
    return row -> {
      int id = Integer.parseInt(row.get(PLAYER_ID_INDEX));
      String firstName = row.get(FIRST_NAME_INDEX);
      String lastName = row.get(LAST_NAME_INDEX);

      return new Player(id, firstName, lastName);
    };
  }
}
