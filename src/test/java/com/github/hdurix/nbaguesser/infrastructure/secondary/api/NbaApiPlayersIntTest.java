package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.IntegrationTest;
import com.github.hdurix.nbaguesser.domain.Player;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class NbaApiPlayersIntTest {

  private static final Logger log = LoggerFactory.getLogger(NbaApiPlayersIntTest.class);

  @Autowired
  private NbaApiPlayers players;

  @Test
  void shouldRetrieveNbaPlayers() {
    Collection<Player> allPlayers = this.players.listAll();

    log.info("{} players found", allPlayers.size());

    assertThat(allPlayers).extracting(Player::fullName).contains("Nic Claxton");
  }
}
