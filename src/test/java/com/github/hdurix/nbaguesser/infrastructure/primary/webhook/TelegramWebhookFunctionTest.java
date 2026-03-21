package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.application.QuizAnswerApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

@UnitTest
@ExtendWith(MockitoExtension.class)
class TelegramWebhookFunctionTest {

  @Mock
  private QuizAnswerApplicationService quizAnswerApplicationService;

  @Test
  void shouldSavePollAnswer() {
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
    String json = """
      {
        "update_id": 1,
        "poll_answer": {
          "poll_id": "abc123",
          "user": { "id": 42, "first_name": "John" },
          "option_ids": [2]
        }
      }
      """;

    String result = function.apply(json);

    assertThat(result).isEqualTo("OK");
    verify(quizAnswerApplicationService).save("abc123", 42L, "John", 2);
  }

  @Test
  void shouldIgnoreRetractedPollAnswer() {
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
    String json = """
      {
        "update_id": 1,
        "poll_answer": {
          "poll_id": "abc123",
          "user": { "id": 42, "first_name": "John" },
          "option_ids": []
        }
      }
      """;

    String result = function.apply(json);

    assertThat(result).isEqualTo("OK");
    verifyNoInteractions(quizAnswerApplicationService);
  }

  @Test
  void shouldReturnOkForStatsCommand() {
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
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
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
    String json = """
      {
        "update_id": 3
      }
      """;

    assertThat(function.apply(json)).isEqualTo("OK");
  }

  @Test
  void shouldReturnOkForInvalidJson() {
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
    assertThat(function.apply("not-json")).isEqualTo("OK");
  }

  @Test
  void shouldIgnoreUnknownFields() {
    TelegramWebhookFunction function = new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService);
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
