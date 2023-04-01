package com.github.hdurix.nbaguesser.infrastructure.secondary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;

class ApiLeague {

  @JsonProperty
  private Collection<ApiPlayer> standard;

  public Collection<ApiPlayer> getStandard() {
    return standard;
  }
}
