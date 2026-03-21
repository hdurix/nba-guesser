package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    server
      .expect(requestTo("https://api.telegram.org/bot123:abc/sendPhoto"))
      .andExpect(method(HttpMethod.POST))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andRespond(withSuccess());
    server
      .expect(requestTo("https://api.telegram.org/bot123:abc/sendPoll"))
      .andExpect(method(HttpMethod.POST))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andRespond(withSuccess("{\"ok\":true,\"result\":{\"message_id\":123,\"poll\":{\"id\":\"5678\"}}}", MediaType.APPLICATION_JSON));

    QuizResult result = telegramQuizzes.send(makeSuggestedPlayers(), makePlayer());

    server.verify();
    assertThat(result.pollId()).isEqualTo("5678");
    assertThat(result.chatId()).isEqualTo("456");
    assertThat(result.messageId()).isEqualTo(123L);
    assertThat(result.options()).hasSize(10);
    assertThat(result.correctOptionIndex()).isEqualTo(2);
    assertThat(result.correctAnswer()).isEqualTo("Nic Claxton");
  }
}
