package com.github.hdurix.nbaguesser;

import com.github.hdurix.nbaguesser.common.domain.Generated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Generated(reason = "Not testing AWS client wiring")
class DynamoDbConfiguration {

  @Bean
  DynamoDbClient dynamoDbClient() {
    return DynamoDbClient.builder().region(Region.EU_WEST_3).build();
  }
}
