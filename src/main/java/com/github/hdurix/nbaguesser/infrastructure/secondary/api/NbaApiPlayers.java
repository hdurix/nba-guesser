package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.domain.Players;
import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.util.Collection;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
class NbaApiPlayers implements Players {

  public static final String PLAYERS_URL =
    "https://gist.githubusercontent.com/hdurix/c83df92b0385c7d3d4c033bfd34e4909/raw/nba-players.json";
  private final RestClient restClient;

  public NbaApiPlayers(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Collection<Player> listAll() {
    ApiPlayersWrapper wrapper = restClient.get().uri(PLAYERS_URL).retrieve().body(ApiPlayersWrapper.class);
    Assert.notNull("players response", wrapper);
    return wrapper.toPlayers();
  }
}
