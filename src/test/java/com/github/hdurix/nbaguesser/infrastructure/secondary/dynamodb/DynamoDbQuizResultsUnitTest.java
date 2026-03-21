package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

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
    assertThat(request.item().get("date").s()).isEqualTo("2026-03-21");
    assertThat(request.item().get("correct_answer").s()).isEqualTo("Nic Claxton");
    assertThat(request.item().get("message_id").n()).isEqualTo("123");
    assertThat(request.item().get("correct_option_index").n()).isEqualTo("2");
    assertThat(request.item().get("options").l()).hasSize(10);
  }
}
