package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.domain.Quizzes;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
class TelegramQuizzes implements Quizzes {

  private final RestClient restClient;
  private final String botId;
  private final String chatId;

  public TelegramQuizzes(RestClient restClient, @Value("${telegram.bot_id}") String botId, @Value("${telegram.chat_id}") String chatId) {
    this.restClient = restClient;
    this.botId = botId;
    this.chatId = chatId;
  }

  @Override
  public void send(Collection<Player> suggestedPlayers, Player correctPlayer) {
    sendPhoto(correctPlayer);
    sendPoll(suggestedPlayers, correctPlayer);
  }

  private void sendPhoto(Player correctPlayer) {
    SendPhotoRequest request = new SendPhotoRequest(
      chatId,
      "https://cdn.nba.com/headshots/nba/latest/1040x760/" + correctPlayer.id() + ".png",
      "\uD83C\uDFC0 Let's play! \uD83E\uDDE0"
    );

    restClient.post().uri(telegramUri("sendPhoto")).contentType(MediaType.APPLICATION_JSON).body(request).retrieve().toBodilessEntity();
  }

  private void sendPoll(Collection<Player> suggestedPlayers, Player correctPlayer) {
    List<String> options = suggestedPlayers.stream().map(Player::fullName).toList();
    int correctOptionId = new ArrayList<>(suggestedPlayers).indexOf(correctPlayer);

    SendPollRequest request = new SendPollRequest(chatId, "Who is this player?", options, correctOptionId, false, "quiz");

    restClient.post().uri(telegramUri("sendPoll")).contentType(MediaType.APPLICATION_JSON).body(request).retrieve().toBodilessEntity();
  }

  private URI telegramUri(String method) {
    return UriComponentsBuilder.fromUriString("https://api.telegram.org/bot").path("{bot_id}/" + method).buildAndExpand(botId).toUri();
  }

  private record SendPhotoRequest(String chat_id, String photo, String caption) {}

  private record SendPollRequest(
    String chat_id,
    String question,
    List<String> options,
    int correct_option_id,
    boolean is_anonymous,
    String type
  ) {}
}
