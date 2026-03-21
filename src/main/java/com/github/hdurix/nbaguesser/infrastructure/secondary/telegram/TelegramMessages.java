package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import com.github.hdurix.nbaguesser.domain.Messages;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
class TelegramMessages implements Messages {

  private final RestClient restClient;
  private final String botId;

  TelegramMessages(RestClient restClient, @Value("${telegram.bot_id}") String botId) {
    this.restClient = restClient;
    this.botId = botId;
  }

  @Override
  public void send(String chatId, String text) {
    SendMessageRequest request = new SendMessageRequest(chatId, text);
    restClient.post().uri(telegramUri("sendMessage")).contentType(MediaType.APPLICATION_JSON).body(request).retrieve().toBodilessEntity();
  }

  private URI telegramUri(String method) {
    return UriComponentsBuilder.fromUriString("https://api.telegram.org/bot").path("{bot_id}/" + method).buildAndExpand(botId).toUri();
  }

  private record SendMessageRequest(String chat_id, String text) {}
}
