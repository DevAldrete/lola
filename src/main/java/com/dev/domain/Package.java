package com.dev.domain;

import java.util.Objects;

public record Package(int id, long priceInCents, int routeId, float weight, Priority priority) {

  public Package {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(priceInCents, "price must not be null");
    Objects.requireNonNull(routeId, "routeId must not be null");
    Objects.requireNonNull(priority, "priority must not be null");

    if (weight < 0) {
      throw new IllegalArgumentException("weight must be non negative");
    }

    if (priceInCents < 0) {
      throw new IllegalArgumentException("priceInCents must be non negative");
    }
  }

  public Package withPriority(Priority priority) {
    return new Package(this.id, this.priceInCents, this.routeId, this.weight, priority);
  }
}
