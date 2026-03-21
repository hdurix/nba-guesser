package com.github.hdurix.nbaguesser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Configuration
class TestDynamoDbConfiguration {

  @Bean
  @Primary
  DynamoDbClient testDynamoDbClient() {
    DynamoDbClient mock = mock(DynamoDbClient.class);
    when(mock.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().build());
    when(mock.scan(any(ScanRequest.class))).thenReturn(ScanResponse.builder().items(java.util.List.of()).build());
    return mock;
  }
}
