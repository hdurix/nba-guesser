package com.github.hdurix.nbaguesser;

import com.github.hdurix.nbaguesser.common.domain.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Generated(reason = "Not testing main")
public class NbaGuesserApp {

  public static void main(String[] args) {
    SpringApplication.run(NbaGuesserApp.class, args);
  }
}
