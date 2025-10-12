package com.github.hdurix.nbaguesser;

import com.github.hdurix.nbaguesser.application.PlayersApplicationService;
import com.github.hdurix.nbaguesser.common.domain.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Generated(reason = "Not testing logs")
public class NbaGuesserApp implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(NbaGuesserApp.class);

  private final PlayersApplicationService players;

  public NbaGuesserApp(PlayersApplicationService players) {
    this.players = players;
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(NbaGuesserApp.class, args);
    Environment env = applicationContext.getEnvironment();

    if (log.isInfoEnabled()) {
      log.info(ApplicationStartupTraces.of(env));
    }
  }

  @Override
  public void run(String... args) {
    players.generateQuizz();
  }
}
