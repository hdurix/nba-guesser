package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.hdurix.nbaguesser.domain.Player;

class ApiPlayer {

  @JsonProperty
  private int personId;

  @JsonProperty
  private String firstName;

  @JsonProperty
  private String lastName;

  @JsonProperty("isActive")
  private boolean active;

  Player toPlayer() {
    return new Player(personId, firstName, lastName);
  }

  public boolean isActive() {
    return active;
  }
}
