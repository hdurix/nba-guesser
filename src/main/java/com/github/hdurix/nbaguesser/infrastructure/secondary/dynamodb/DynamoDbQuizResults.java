package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import com.github.hdurix.nbaguesser.domain.QuizResult;
import com.github.hdurix.nbaguesser.domain.QuizResults;
import java.util.List;
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
    dynamoDbClient.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(toItem(result)).build());
  }

  private static Map<String, AttributeValue> toItem(QuizResult result) {
    return Map.of(
      "poll_id",
      stringAttr(result.pollId()),
      "date",
      stringAttr(result.date().toString()),
      "options",
      listAttr(result.options()),
      "correct_option_index",
      numberAttr(result.correctOptionIndex()),
      "correct_answer",
      stringAttr(result.correctAnswer()),
      "message_id",
      numberAttr(result.messageId())
    );
  }

  private static AttributeValue stringAttr(String value) {
    return AttributeValue.builder().s(value).build();
  }

  private static AttributeValue numberAttr(long value) {
    return AttributeValue.builder().n(String.valueOf(value)).build();
  }

  private static AttributeValue listAttr(List<String> values) {
    return AttributeValue.builder().l(values.stream().map(DynamoDbQuizResults::stringAttr).toList()).build();
  }
}
