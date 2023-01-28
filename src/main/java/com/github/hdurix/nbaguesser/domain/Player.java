package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;

public record Player(int id, String firstName, String lastName) {
  public Player {
    Assert.notBlank("firstName", firstName);
    Assert.notBlank("lastName", lastName);
  }
}
