package com.dev.domain;

import java.util.Objects;

public record Vehicle(int id, String plate, float capacityKg) implements Comparable<Vehicle> {

  public Vehicle {
    Objects.requireNonNull(plate, "plate must not be null");

    if (plate.isBlank()) {
      throw new IllegalArgumentException("plate must not be blank");
    }

    if (capacityKg < 0) {
      throw new IllegalArgumentException("capacityKg must be non negative");
    }
  }

  @Override
  public int compareTo(Vehicle other) {
    int byCapacity = Float.compare(capacityKg, other.capacityKg);

    return byCapacity != 0 ? byCapacity : Integer.compare(id, other.id);
  }
}
