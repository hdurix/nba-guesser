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
              [
                2,
                "Claxton",
                "Nic",
                "nic-claxton",
                1610612751,
                "nets",
                0,
                "Brooklyn",
                "Nets",
                "BKN",
                "33",
                "C",
                "6-11",
                "215",
                "Georgia",
                "USA",
                2019,
                2,
                31,
                1,
                12.200,
                10.200,
                1.900,
                "Season",
                2019,
                2023
              ],
              [
                1629623,
                "Wigginton",
                "Lindell",
                "lindell-wigginton",
                0,
                null,
                0,
                null,
                null,
                null,
                null,
                "G",
                "6-1",
                "189",
                "Iowa State",
                "Canada",
                2019,
                null,
                null,
                null,
                2.000,
                0.000,
                0.000,
                "Season",
                2021,
                2023
              ],
              [
                9999,
                "Retired",
                "Player",
                "player-retired",
                0,
                null,
                0,
                null,
                null,
                null,
                null,
                "G",
                "6-0",
                "180",
                null,
                "USA",
                2010,
                null,
                null,
                0,
                0.000,
                0.000,
                0.000,
                "Season",
                2010,
                2020
              ]
            ]
          }
        ]
      }
      """;
  }
}
