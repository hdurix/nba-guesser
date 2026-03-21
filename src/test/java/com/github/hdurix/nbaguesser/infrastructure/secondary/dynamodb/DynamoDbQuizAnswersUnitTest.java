package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import static com.github.hdurix.nbaguesser.domain.QuizAnswerFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
class DynamoDbQuizAnswersUnitTest {

  @Mock
  private DynamoDbClient dynamoDbClient;

  @InjectMocks
  private DynamoDbQuizAnswers dynamoDbQuizAnswers;

  @Test
  void shouldSaveQuizAnswer() {
    QuizAnswer quizAnswer = makeQuizAnswer();

    dynamoDbQuizAnswers.save(quizAnswer);

    ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
    verify(dynamoDbClient).putItem(captor.capture());

    PutItemRequest request = captor.getValue();
    assertThat(request.tableName()).isEqualTo("nba-guesser-answers");
    assertThat(request.item().get("poll_id").s()).isEqualTo("5678");
    assertThat(request.item().get("user_id").n()).isEqualTo("123456789");
    assertThat(request.item().get("user_name").s()).isEqualTo("John");
    assertThat(request.item().get("option_index").n()).isEqualTo("2");
    assertThat(request.item().get("timestamp").s()).isEqualTo("2026-03-21T08:30:00Z");
  }

  @Test
  void shouldFindByPollIds() {
    Map<String, AttributeValue> item1 = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "user_id",
      AttributeValue.builder().n("42").build(),
      "user_name",
      AttributeValue.builder().s("Alice").build(),
      "option_index",
      AttributeValue.builder().n("2").build(),
      "timestamp",
      AttributeValue.builder().s("2026-03-21T08:30:00Z").build()
    );
    Map<String, AttributeValue> item2 = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll2").build(),
      "user_id",
      AttributeValue.builder().n("43").build(),
      "user_name",
      AttributeValue.builder().s("Bob").build(),
      "option_index",
      AttributeValue.builder().n("0").build(),
      "timestamp",
      AttributeValue.builder().s("2026-03-21T09:00:00Z").build()
    );
    when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(List.of(item1, item2)).build());

    List<QuizAnswer> answers = dynamoDbQuizAnswers.findByPollIds(Set.of("poll1"));

    assertThat(answers).hasSize(1);
    QuizAnswer answer = answers.getFirst();
    assertThat(answer.pollId()).isEqualTo("poll1");
    assertThat(answer.userId()).isEqualTo(42L);
    assertThat(answer.userName()).isEqualTo("Alice");
    assertThat(answer.optionIndex()).isEqualTo(2);
  }

  @Test
  void shouldSkipLegacyItemsWithoutTimestamp() {
    Map<String, AttributeValue> legacyItem = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "user_id",
      AttributeValue.builder().n("42").build(),
      "user_name",
      AttributeValue.builder().s("Alice").build(),
      "option_index",
      AttributeValue.builder().n("2").build(),
      "date",
      AttributeValue.builder().s("2024-12-10T06:31:01+00:00").build(),
      "correct",
      AttributeValue.builder().bool(true).build()
    );
    Map<String, AttributeValue> newItem = Map.of(
      "poll_id",
      AttributeValue.builder().s("poll1").build(),
      "user_id",
      AttributeValue.builder().n("43").build(),
      "user_name",
      AttributeValue.builder().s("Bob").build(),
      "option_index",
      AttributeValue.builder().n("1").build(),
      "timestamp",
      AttributeValue.builder().s("2026-03-21T08:30:00Z").build()
    );
    when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(List.of(legacyItem, newItem)).build());

    List<QuizAnswer> answers = dynamoDbQuizAnswers.findByPollIds(Set.of("poll1"));

    assertThat(answers).hasSize(1);
    assertThat(answers.getFirst().userName()).isEqualTo("Bob");
  }
}
