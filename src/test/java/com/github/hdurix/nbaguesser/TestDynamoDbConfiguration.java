package com.github.hdurix.nbaguesser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Configuration
class TestDynamoDbConfiguration {

  @Bean
  @Primary
  DynamoDbClient testDynamoDbClient() {
    DynamoDbClient mock = mock(DynamoDbClient.class);
    when(mock.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().build());
    return mock;
  }
}
