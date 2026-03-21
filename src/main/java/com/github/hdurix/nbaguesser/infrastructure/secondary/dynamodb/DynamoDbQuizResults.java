package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import com.github.hdurix.nbaguesser.domain.QuizResult;
import com.github.hdurix.nbaguesser.domain.QuizResults;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
class DynamoDbQuizResults implements QuizResults {

  private static final String TABLE_NAME = "nba-guesser-quizzes";

  private final DynamoDbClient dynamoDbClient;

  DynamoDbQuizResults(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
  }

  @Override
  public void save(QuizResult result) {
    Map<String, AttributeValue> item = Map.of(
      "poll_id",
      AttributeValue.builder().s(result.pollId()).build(),
      "date",
      AttributeValue.builder().s(result.date().toString()).build(),
      "options",
      AttributeValue.builder()
        .l(
          result
            .options()
            .stream()
            .map(option -> AttributeValue.builder().s(option).build())
            .toList()
        )
        .build(),
      "correct_option_index",
      AttributeValue.builder().n(String.valueOf(result.correctOptionIndex())).build(),
      "correct_answer",
      AttributeValue.builder().s(result.correctAnswer()).build(),
      "message_id",
      AttributeValue.builder().n(String.valueOf(result.messageId())).build()
    );

    dynamoDbClient.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(item).build());
  }
}
