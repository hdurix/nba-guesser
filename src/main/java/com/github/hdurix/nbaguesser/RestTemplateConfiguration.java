package com.github.hdurix.nbaguesser;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
class RestTemplateConfiguration {

  @Bean
  RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(List.of(MediaType.ALL));

    restTemplate.setMessageConverters(List.of(converter));

    return restTemplate;
  }
}
