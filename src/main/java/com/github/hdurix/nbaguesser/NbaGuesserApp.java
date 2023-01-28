package com.github.hdurix.nbaguesser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.github.hdurix.nbaguesser.common.domain.Generated;

@SpringBootApplication
@Generated(reason = "Not testing logs")
public class NbaGuesserApp {

  private static final Logger log = LoggerFactory.getLogger(NbaGuesserApp.class);

  public static void main(String[] args) {
    Environment env = SpringApplication.run(NbaGuesserApp.class, args).getEnvironment();

    if (log.isInfoEnabled()) {
      log.info(ApplicationStartupTraces.of(env));
    }
  }
}
