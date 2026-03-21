package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record TelegramUpdate(long update_id, PollAnswer poll_answer, Message message) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  record PollAnswer(String poll_id, User user, List<Integer> option_ids) {
    QuizAnswer toQuizAnswer() {
      return QuizAnswer.builder()
        .pollId(poll_id)
        .userId(user.id())
        .userName(user.first_name())
        .optionIndex(option_ids.getFirst())
        .timestamp(Instant.now())
        .build();
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Message(long message_id, Chat chat, String text, User from) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  record User(long id, String first_name) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Chat(long id, String type) {}

  boolean isPollAnswer() {
    return poll_answer != null;
  }

  boolean isStatsCommand() {
    return message != null && message.text() != null && message.text().startsWith("/stats");
  }
}
