package com.github.hdurix.nbaguesser;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
class RestClientConfiguration {

  @Bean
  RestClient restClient() {
    JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter();
    converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));

    return RestClient.builder()
      .messageConverters(converters -> {
        converters.clear();
        converters.add(converter);
      })
      .build();
  }
}
