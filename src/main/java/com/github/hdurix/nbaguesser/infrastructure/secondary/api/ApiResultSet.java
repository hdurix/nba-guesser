package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class ApiResultSet {

  public static final String PLAYING_ROSTER_STATUS = "1.0";

  @JsonProperty
  private Collection<List<String>> rowSet;

  public Collection<Player> toPlayers() {
    return rowSet.stream().filter(isActivePlayer()).map(toPlayer()).toList();
  }

  private static Predicate<List<String>> isActivePlayer() {
    return row -> {
      String rosterStatus = row.get(19);

      return PLAYING_ROSTER_STATUS.equals(rosterStatus);
    };
  }

  private static Function<List<String>, Player> toPlayer() {
    return row -> {
      int id = Integer.parseInt(row.get(0));
      String firstName = row.get(2);
      String lastName = row.get(1);

      return new Player(id, firstName, lastName);
    };
  }
}
