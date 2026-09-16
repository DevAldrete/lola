package com.dev.domain;

import java.util.Objects;

public record Zone(int id, String state, String city) {
  public Zone {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(state, "state must not be null");
    Objects.requireNonNull(city, "city must not be null");
  }
}
