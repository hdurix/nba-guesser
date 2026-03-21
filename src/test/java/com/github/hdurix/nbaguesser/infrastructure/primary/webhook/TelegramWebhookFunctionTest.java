package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import static org.assertj.core.api.Assertions.*;

import com.github.hdurix.nbaguesser.UnitTest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

@UnitTest
class TelegramWebhookFunctionTest {

  private final TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build());

  @Test
  void shouldReturnOkForPollAnswer() {
    String json = """
      {
        "update_id": 1,
        "poll_answer": {
          "poll_id": "abc123",
          "user": { "id": 42, "first_name": "John" },
          "option_ids": [0]
        }
      }
      """;

    assertThat(function.apply(json)).isEqualTo("OK");
  }

  @Test
  void shouldReturnOkForStatsCommand() {
    String json = """
      {
        "update_id": 2,
        "message": {
          "message_id": 10,
          "chat": { "id": 100, "type": "group" },
          "text": "/stats",
          "from": { "id": 42, "first_name": "John" }
        }
      }
      """;

    assertThat(function.apply(json)).isEqualTo("OK");
  }

  @Test
  void shouldReturnOkForUnhandledUpdateType() {
    String json = """
      {
        "update_id": 3
      }
      """;

    assertThat(function.apply(json)).isEqualTo("OK");
  }

  @Test
  void shouldReturnOkForInvalidJson() {
    assertThat(function.apply("not-json")).isEqualTo("OK");
  }

  @Test
  void shouldIgnoreUnknownFields() {
    String json = """
      {
        "update_id": 4,
        "unknown_field": "value",
        "poll_answer": {
          "poll_id": "abc",
          "user": { "id": 1, "first_name": "A", "is_bot": false },
          "option_ids": [1]
        }
      }
      """;

    assertThat(function.apply(json)).isEqualTo("OK");
  }
}
