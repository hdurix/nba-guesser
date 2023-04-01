package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.IntegrationTest;
import com.github.hdurix.nbaguesser.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class NbaApiPlayersIntTest {

  @Autowired
  private NbaApiPlayers players;

  @Test
  void shouldRetrieveNbaPlayers() {
    assertThat(players.listAll()).extracting(Player::fullName).contains("Nic Claxton");
  }
}
