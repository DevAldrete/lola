package com.dev.domain;

import java.util.Objects;

public record Vehicle(int id, String plate, float capacityKg) {

  public Vehicle {
    Objects.requireNonNull(plate, "plate must not be null");

    if (plate.isBlank()) {
      throw new IllegalArgumentException("plate must not be blank");
    }

    if (capacityKg < 0) {
      throw new IllegalArgumentException("capacityKg must be non negative");
    }
  }
}
