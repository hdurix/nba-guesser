package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
class DynamoDbQuizAnswers implements QuizAnswers {

  private static final String TABLE_NAME = "nba-guesser-answers";

  private final DynamoDbClient dynamoDbClient;

  DynamoDbQuizAnswers(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
  }

  @Override
  public void save(QuizAnswer answer) {
    dynamoDbClient.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(toItem(answer)).build());
  }

  private static Map<String, AttributeValue> toItem(QuizAnswer answer) {
    return Map.of(
      "poll_id",
      stringAttr(answer.pollId()),
      "user_id",
      numberAttr(answer.userId()),
      "user_name",
      stringAttr(answer.userName()),
      "option_index",
      numberAttr(answer.optionIndex()),
      "timestamp",
      stringAttr(answer.timestamp().toString())
    );
  }

  private static AttributeValue stringAttr(String value) {
    return AttributeValue.builder().s(value).build();
  }

  private static AttributeValue numberAttr(long value) {
    return AttributeValue.builder().n(String.valueOf(value)).build();
  }
}
