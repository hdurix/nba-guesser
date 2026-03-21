package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import com.github.hdurix.nbaguesser.domain.QuizAnswer;
import com.github.hdurix.nbaguesser.domain.QuizAnswers;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

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

  @Override
  public List<QuizAnswer> findByPollIds(Collection<String> pollIds) {
    ScanResponse response = dynamoDbClient.scan(ScanRequest.builder().tableName(TABLE_NAME).build());

    return response
      .items()
      .stream()
      .filter(item -> item.containsKey("timestamp"))
      .map(DynamoDbQuizAnswers::fromItem)
      .filter(answer -> pollIds.contains(answer.pollId()))
      .toList();
  }

  private static QuizAnswer fromItem(Map<String, AttributeValue> item) {
    return QuizAnswer
      .builder()
      .pollId(item.get("poll_id").s())
      .userId(Long.parseLong(item.get("user_id").n()))
      .userName(item.get("user_name").s())
      .optionIndex(Integer.parseInt(item.get("option_index").n()))
      .timestamp(Instant.parse(item.get("timestamp").s()))
      .build();
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
