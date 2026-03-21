package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import java.util.List;
import org.junit.jupiter.api.Test;

@UnitTest
class TelegramUpdateTest {

  @Test
  void shouldDetectPollAnswer() {
    TelegramUpdate update = new TelegramUpdate(
      1L,
      new TelegramUpdate.PollAnswer("poll1", new TelegramUpdate.User(42L, "John"), List.of(0)),
      null
    );

    assertThat(update.isPollAnswer()).isTrue();
    assertThat(update.isStatsCommand()).isFalse();
  }

  @Test
  void shouldDetectStatsCommand() {
    TelegramUpdate update = new TelegramUpdate(
      2L,
      null,
      new TelegramUpdate.Message(10L, new TelegramUpdate.Chat(100L, "group"), "/stats", new TelegramUpdate.User(42L, "John"))
    );

    assertThat(update.isPollAnswer()).isFalse();
    assertThat(update.isStatsCommand()).isTrue();
  }

  @Test
  void shouldDetectStatsCommandWithArguments() {
    TelegramUpdate update = new TelegramUpdate(
      3L,
      null,
      new TelegramUpdate.Message(11L, new TelegramUpdate.Chat(100L, "group"), "/stats week", new TelegramUpdate.User(42L, "John"))
    );

    assertThat(update.isStatsCommand()).isTrue();
  }

  @Test
  void shouldNotDetectStatsCommandForRegularMessage() {
    TelegramUpdate update = new TelegramUpdate(
      4L,
      null,
      new TelegramUpdate.Message(12L, new TelegramUpdate.Chat(100L, "group"), "hello", new TelegramUpdate.User(42L, "John"))
    );

    assertThat(update.isPollAnswer()).isFalse();
    assertThat(update.isStatsCommand()).isFalse();
  }

  @Test
  void shouldHandleNullMessage() {
    TelegramUpdate update = new TelegramUpdate(5L, null, null);

    assertThat(update.isPollAnswer()).isFalse();
    assertThat(update.isStatsCommand()).isFalse();
  }

  @Test
  void shouldHandleMessageWithNullText() {
    TelegramUpdate update = new TelegramUpdate(
      6L,
      null,
      new TelegramUpdate.Message(13L, new TelegramUpdate.Chat(100L, "group"), null, new TelegramUpdate.User(42L, "John"))
    );

    assertThat(update.isStatsCommand()).isFalse();
  }
}
