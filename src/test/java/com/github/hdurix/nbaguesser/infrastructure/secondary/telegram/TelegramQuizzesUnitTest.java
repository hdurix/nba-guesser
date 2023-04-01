package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@UnitTest
@ExtendWith(MockitoExtension.class)
class TelegramQuizzesUnitTest {

  @Mock
  private RestTemplate restTemplate;

  private TelegramQuizzes telegramQuizzes;

  @BeforeEach
  void setup() {
    telegramQuizzes = new TelegramQuizzes(restTemplate, "123:abc", "456");
  }

  @Test
  void shouldSendToTelegramBot() {
    telegramQuizzes.send(makeSuggestedPlayers(), makePlayer());

    ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
    verify(restTemplate, times(2)).getForEntity(captor.capture(), eq(Object.class));

    List<URI> urls = captor.getAllValues();
    assertThat(urls.get(0))
      .hasToString(
        "https://api.telegram.org/bot123:abc/sendPhoto?chat_id=456&photo=https://cdn.nba.com/headshots/nba/latest/1040x760/2.png&caption=🏀%20Let's%20play!%20🧠"
      );
    String suggestedPlayers = IntStream.range(0, 10).mapToObj(i -> "%22Nic%20Claxton%22").collect(Collectors.joining(","));
    assertThat(urls.get(1))
      .hasToString(
        "https://api.telegram.org/bot123:abc/sendPoll?chat_id=456&question=Who%20is%20this%20player?&options=[" +
        suggestedPlayers +
        "]&correct_option_id=2&is_anonymous=false&type=quiz"
      );
  }
}
