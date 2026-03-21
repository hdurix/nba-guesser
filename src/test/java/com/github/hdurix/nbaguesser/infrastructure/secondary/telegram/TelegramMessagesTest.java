package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.github.hdurix.nbaguesser.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@UnitTest
class TelegramMessagesTest {

  private MockRestServiceServer server;
  private TelegramMessages telegramMessages;

  @Test
  void shouldSendMessage() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    telegramMessages = new TelegramMessages(builder.build(), "123:abc");

    server
      .expect(requestTo("https://api.telegram.org/bot123:abc/sendMessage"))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andRespond(withSuccess());

    telegramMessages.send("456", "Hello");

    server.verify();
  }
}
