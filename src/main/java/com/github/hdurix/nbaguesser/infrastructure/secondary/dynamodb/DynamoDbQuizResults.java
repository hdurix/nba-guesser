package com.github.hdurix.nbaguesser.infrastructure.secondary.dynamodb;

import com.github.hdurix.nbaguesser.domain.QuizResult;
import com.github.hdurix.nbaguesser.domain.QuizResults;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

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

  @Override
  public List<QuizResult> findByChatId(String chatId) {
    ScanResponse response = dynamoDbClient.scan(
      ScanRequest.builder()
        .tableName(TABLE_NAME)
        .filterExpression("chat_id = :chatId")
        .expressionAttributeValues(Map.of(":chatId", stringAttr(chatId)))
        .build()
    );

    return response
      .items()
      .stream()
      .filter(item -> item.containsKey("correct_option_index"))
      .map(DynamoDbQuizResults::fromItem)
      .toList();
  }

  private static QuizResult fromItem(Map<String, AttributeValue> item) {
    return QuizResult.builder()
      .pollId(item.get("poll_id").s())
      .chatId(item.get("chat_id").s())
      .date(parseDate(item.get("date").s()))
      .options(item.get("options").l().stream().map(AttributeValue::s).toList())
      .correctOptionIndex(Integer.parseInt(item.get("correct_option_index").n()))
      .correctAnswer(item.get("correct_answer").s())
      .messageId(Long.parseLong(item.get("message_id").n()))
      .build();
  }

  private static Map<String, AttributeValue> toItem(QuizResult result) {
    return Map.of(
      "poll_id",
      stringAttr(result.pollId()),
      "chat_id",
      stringAttr(result.chatId()),
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

  private static LocalDate parseDate(String dateString) {
    try {
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException e) {
      return OffsetDateTime.parse(dateString).toLocalDate();
    }
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
