package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DynamoDbQuizResultsUnitTest {

  @Mock
  private DynamoDbClient dynamoDbClient;

  @InjectMocks
  private DynamoDbQuizResults dynamoDbQuizResults;

  @Test
  void shouldSaveQuizResult() {
    QuizResult quizResult = makeQuizResult();

    dynamoDbQuizResults.save(quizResult);

    ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
    verify(dynamoDbClient).putItem(captor.capture());

    PutItemRequest request = captor.getValue();
    assertThat(request.tableName()).isEqualTo("nba-guesser-quizzes");
    assertThat(request.item().get("poll_id").s()).isEqualTo("5678");
    assertThat(request.item().get("chat_id").s()).isEqualTo("456");
    assertThat(request.item().get("date").s()).isEqualTo("2026-03-21");
    assertThat(request.item().get("correct_answer").s()).isEqualTo("Nic Claxton");
    assertThat(request.item().get("message_id").n()).isEqualTo("123");
    assertThat(request.item().get("correct_option_index").n()).isEqualTo("2");
    assertThat(request.item().get("options").l()).hasSize(10);
  }

  @Test
  void shouldFindByChatId() {
    Map<String, AttributeValue> item = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "chat_id",
      AttributeValue.builder().s("456").build(),
      "date",
      AttributeValue.builder().s("2026-03-21").build(),
      "options",
      AttributeValue.builder()
        .l(List.of(AttributeValue.builder().s("Player A").build(), AttributeValue.builder().s("Player B").build()))
        .build(),
      "correct_option_index",
      AttributeValue.builder().n("1").build(),
      "correct_answer",
      AttributeValue.builder().s("Player B").build(),
      "message_id",
      AttributeValue.builder().n("42").build()
    );
    when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(List.of(item)).build());

    List<QuizResult> results = dynamoDbQuizResults.findByChatId("456");

    assertThat(results).hasSize(1);
    QuizResult result = results.getFirst();
    assertThat(result.pollId()).isEqualTo("poll1");
    assertThat(result.chatId()).isEqualTo("456");
    assertThat(result.correctOptionIndex()).isEqualTo(1);
    assertThat(result.correctAnswer()).isEqualTo("Player B");
    assertThat(result.messageId()).isEqualTo(42L);

    ArgumentCaptor<ScanRequest> captor = ArgumentCaptor.forClass(ScanRequest.class);
    verify(dynamoDbClient).scan(captor.capture());
    assertThat(captor.getValue().tableName()).isEqualTo("nba-guesser-quizzes");
    assertThat(captor.getValue().filterExpression()).isEqualTo("chat_id = :chatId");
  }

  @Test
  void shouldParseLegacyDateFormat() {
    Map<String, AttributeValue> item = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "chat_id",
      AttributeValue.builder().s("456").build(),
      "date",
      AttributeValue.builder().s("2024-12-10T06:31:01+00:00").build(),
      "options",
      AttributeValue.builder().l(List.of(AttributeValue.builder().s("Player A").build())).build(),
      "correct_option_index",
      AttributeValue.builder().n("0").build(),
      "correct_answer",
      AttributeValue.builder().s("Player A").build(),
      "message_id",
      AttributeValue.builder().n("1").build()
    );
    when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(List.of(item)).build());

    List<QuizResult> results = dynamoDbQuizResults.findByChatId("456");

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().date().toString()).isEqualTo("2024-12-10");
  }

  @Test
  void shouldSkipItemsWithoutCorrectOptionIndex() {
    Map<String, AttributeValue> validItem = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "chat_id",
      AttributeValue.builder().s("456").build(),
      "date",
      AttributeValue.builder().s("2026-03-21").build(),
      "options",
      AttributeValue.builder().l(List.of(AttributeValue.builder().s("Player A").build())).build(),
      "correct_option_index",
      AttributeValue.builder().n("0").build(),
      "correct_answer",
      AttributeValue.builder().s("Player A").build(),
      "message_id",
      AttributeValue.builder().n("1").build()
    );
    Map<String, AttributeValue> legacyItem = new HashMap<>();
    legacyItem.put("poll_id", AttributeValue.builder().s("poll2").build());
    legacyItem.put("chat_id", AttributeValue.builder().s("456").build());
    legacyItem.put("date", AttributeValue.builder().s("2024-01-01T00:00:00+00:00").build());
    legacyItem.put("correct_answer", AttributeValue.builder().s("?").build());
    legacyItem.put("message_id", AttributeValue.builder().n("2").build());
    legacyItem.put("options", AttributeValue.builder().l(List.of(AttributeValue.builder().s("A").build())).build());

    when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(List.of(validItem, legacyItem)).build());

    List<QuizResult> results = dynamoDbQuizResults.findByChatId("456");

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().pollId()).isEqualTo("poll1");
  }
}
