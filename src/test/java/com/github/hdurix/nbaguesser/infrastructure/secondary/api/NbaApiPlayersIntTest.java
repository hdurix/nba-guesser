package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.IntegrationTest;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("NBA data.nba.net API is deprecated and returns 400")
@IntegrationTest
class NbaApiPlayersIntTest {

  @Autowired
  private NbaApiPlayers players;

  @Test
  void shouldRetrieveNbaPlayers() {
    Collection<Player> allPlayers = this.players.listAll();

    System.out.println(allPlayers.size() + " players found");

    assertThat(allPlayers).extracting(Player::fullName).contains("Nic Claxton");
  }
}
