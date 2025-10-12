package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.domain.Players;
import java.util.Collection;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
class NbaApiPlayers implements Players {

  public static final String PLAYERS_URL =
    "https://gist.githubusercontent.com/hdurix/c83df92b0385c7d3d4c033bfd34e4909/raw/5788406b2ea5365eb6b7731c9cbc36d3db84d94f/nba-players.json";
  private final RestTemplate restTemplate;

  public NbaApiPlayers(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public Collection<Player> listAll() {
    return restTemplate.getForObject(PLAYERS_URL, ApiPlayersWrapper.class).toPlayers();
  }
}
