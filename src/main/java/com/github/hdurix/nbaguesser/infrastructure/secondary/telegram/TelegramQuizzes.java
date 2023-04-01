package com.github.hdurix.nbaguesser.infrastructure.secondary.telegram;

import com.github.hdurix.nbaguesser.domain.Player;
import com.github.hdurix.nbaguesser.domain.Quizzes;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
class TelegramQuizzes implements Quizzes {

  private final RestTemplate restTemplate;
  private final String botId;
  private final String chatId;

  public TelegramQuizzes(
    RestTemplate restTemplate,
    @Value("${telegram.bot_id}") String botId,
    @Value("${telegram.chat_id}") String chatId
  ) {
    this.restTemplate = restTemplate;
    this.botId = botId;
    this.chatId = chatId;
  }

  @Override
  public void send(Collection<Player> suggestedPlayers, Player correctPlayer) {
    sendPhoto(correctPlayer);

    sendPoll(suggestedPlayers, correctPlayer);
  }

  private void sendPhoto(Player correctPlayer) {
    URI uri = UriComponentsBuilder
      .fromUriString("https://api.telegram.org/bot")
      .path("{bot_id}/sendPhoto")
      .queryParam("chat_id", chatId)
      .queryParam("photo", "https://cdn.nba.com/headshots/nba/latest/1040x760/" + correctPlayer.id() + ".png")
      .queryParam("caption", "🏀 Let's play! 🧠")
      .buildAndExpand(botId)
      .toUri();

    restTemplate.getForEntity(uri, Object.class);
  }

  private void sendPoll(Collection<Player> suggestedPlayers, Player correctPlayer) {
    String options = suggestedPlayers.stream().map(Player::fullName).collect(Collectors.joining("\",\"", "[\"", "\"]"));

    int correctOptionId = new ArrayList<>(suggestedPlayers).indexOf(correctPlayer);

    URI uri = UriComponentsBuilder
      .fromUriString("https://api.telegram.org/bot")
      .path("{bot_id}/sendPoll")
      .queryParam("chat_id", chatId)
      .queryParam("question", "Who is this player?")
      .queryParam("options", options)
      .queryParam("correct_option_id", correctOptionId)
      .queryParam("is_anonymous", false)
      .queryParam("type", "quiz")
      .buildAndExpand(botId)
      .toUri();

    restTemplate.getForEntity(uri, Object.class);
  }

  private static Function<Player, String> toPlayerOption() {
    return player -> "\"" + player.fullName() + "\"";
  }
}
