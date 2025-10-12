package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;
import java.util.List;

class ApiPlayersWrapper {

  @JsonProperty
  private Collection<ApiResultSet> resultSets;

  public Collection<Player> toPlayers() {
    return resultSets.iterator().next().toPlayers();
  }
}
