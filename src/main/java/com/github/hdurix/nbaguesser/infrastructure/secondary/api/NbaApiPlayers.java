package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.domain.Players;
import java.util.Collection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
class NbaApiPlayers implements Players {

  private final RestTemplate restTemplate;

  public NbaApiPlayers(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public Collection<Player> listAll() {
    String url = "http://data.nba.net/data/10s/prod/v1/2022/players.json";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "*");

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    return restTemplate.exchange(url, HttpMethod.GET, entity, ApiPlayersWrapper.class).getBody().toPlayers();
  }
}
