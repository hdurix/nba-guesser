package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;

class ApiPlayersWrapper {

  @JsonProperty
  private ApiLeague league;

  public Collection<Player> toPlayers() {
    return league.getStandard().stream().filter(ApiPlayer::isActive).map(ApiPlayer::toPlayer).toList();
  }
}
