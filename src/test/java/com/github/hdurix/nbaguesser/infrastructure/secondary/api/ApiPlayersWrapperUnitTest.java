package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.JsonHelper;
import com.github.hdurix.nbaguesser.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class ApiPlayersWrapperUnitTest {

  @Test
  void shouldMapToDomain() {
    assertThat(JsonHelper.readFromJson(json(), ApiPlayersWrapper.class).toPlayers()).containsOnly(makePlayer());
  }

  static String json() {
    return """
      {
        "resultSets": [
          {
            "rowSet": [
              [2, "Claxton", "Nic", 1],
              [1629623, "Wigginton", "Lindell", null],
              [9999, "Retired", "Player", 0]
            ]
          }
        ]
      }
      """;
  }
}
