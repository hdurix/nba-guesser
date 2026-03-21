package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.application.QuizAnswerApplicationService;
import com.github.hdurix.nbaguesser.application.StatsApplicationService;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

@UnitTest
@ExtendWith(MockitoExtension.class)
class TelegramWebhookFunctionTest {

  @Mock
  private QuizAnswerApplicationService quizAnswerApplicationService;

  @Mock
  private StatsApplicationService statsApplicationService;

  private TelegramWebhookFunction function() {
    return new TelegramWebhookFunction(JsonMapper.builder().build(), quizAnswerApplicationService, statsApplicationService);
  }

  @Test
  void shouldSavePollAnswer() {
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

    String result = function().apply(json);

    assertThat(result).isEqualTo("OK");
    ArgumentCaptor<QuizAnswer> captor = ArgumentCaptor.forClass(QuizAnswer.class);
    verify(quizAnswerApplicationService).save(captor.capture());
    QuizAnswer saved = captor.getValue();
    assertThat(saved.pollId()).isEqualTo("abc123");
    assertThat(saved.userId()).isEqualTo(42L);
    assertThat(saved.userName()).isEqualTo("John");
    assertThat(saved.optionIndex()).isEqualTo(2);
    assertThat(saved.timestamp()).isNotNull();
  }

  @Test
  void shouldIgnoreRetractedPollAnswer() {
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

    String result = function().apply(json);

    assertThat(result).isEqualTo("OK");
    verifyNoInteractions(quizAnswerApplicationService);
  }

  @Test
  void shouldSendStatsForStatsCommand() {
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

    String result = function().apply(json);

    assertThat(result).isEqualTo("OK");
    verify(statsApplicationService).sendStats("100");
  }

  @Test
  void shouldReturnOkForUnhandledUpdateType() {
    String json = """
      {
        "update_id": 3
      }
      """;

    assertThat(function().apply(json)).isEqualTo("OK");
  }

  @Test
  void shouldReturnOkForInvalidJson() {
    assertThat(function().apply("not-json")).isEqualTo("OK");
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

    assertThat(function().apply(json)).isEqualTo("OK");
  }
}
