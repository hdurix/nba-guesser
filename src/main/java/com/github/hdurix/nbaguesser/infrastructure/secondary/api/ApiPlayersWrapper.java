package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.util.Collection;

class ApiPlayersWrapper {

  @JsonProperty
  private Collection<ApiResultSet> resultSets;

  public Collection<Player> toPlayers() {
    Assert.notEmpty("resultSets", resultSets);
    return resultSets.iterator().next().toPlayers();
  }
}
