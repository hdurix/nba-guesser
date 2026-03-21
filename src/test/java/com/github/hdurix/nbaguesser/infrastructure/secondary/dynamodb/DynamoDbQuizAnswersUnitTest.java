package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import static com.github.hdurix.nbaguesser.domain.QuizAnswerFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.hdurix.nbaguesser.UnitTest;
import com.github.hdurix.nbaguesser.domain.QuizAnswer;
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
}
