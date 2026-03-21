package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.github.hdurix.nbaguesser.UnitTest;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@UnitTest
class TelegramQuizzesUnitTest {

  private MockRestServiceServer server;
  private TelegramQuizzes telegramQuizzes;

  @BeforeEach
  void setup() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    telegramQuizzes = new TelegramQuizzes(builder.build(), "123:abc", "456");
  }

  @Test
  void shouldSendToTelegramBot() {
    String suggestedPlayers = IntStream.range(0, 10)
      .mapToObj(i -> "%22Nic%20Claxton%22")
      .collect(Collectors.joining(","));

    server
      .expect(
        requestTo(
          URI.create(
            "https://api.telegram.org/bot123:abc/sendPhoto?chat_id=456&photo=https://cdn.nba.com/headshots/nba/latest/1040x760/2.png&caption=🏀%20Let's%20play!%20🧠"
          )
        )
      )
      .andRespond(withSuccess());
    server
      .expect(
        requestTo(
          URI.create(
            "https://api.telegram.org/bot123:abc/sendPoll?chat_id=456&question=Who%20is%20this%20player?&options=[" +
              suggestedPlayers +
              "]&correct_option_id=2&is_anonymous=false&type=quiz"
          )
        )
      )
      .andRespond(withSuccess());

    telegramQuizzes.send(makeSuggestedPlayers(), makePlayer());

    server.verify();
  }
}
