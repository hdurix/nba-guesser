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
        "league": {
          "standard": [
            {
              "firstName": "Nic",
              "lastName": "Claxton",
              "personId": "2",
              "teamId": "1610612751",
              "isActive": true
            },
            {
              "firstName": "Briante",
              "lastName": "Weber",
              "personId": "1",
              "teamId": "1610612751",
              "isActive": false
            }
          ]
        }
      }
      """;
  }
}
