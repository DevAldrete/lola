package com.dev.domain;

import java.time.Duration;
import java.util.Objects;

public record Route(int id, int originId, int destinyId, double distanceMeters, Duration estimatedTime,
    long expenseInCents) {
  public Route {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(originId, "originId must not be null");
    Objects.requireNonNull(destinyId, "destinyId must not be null");
    Objects.requireNonNull(distanceMeters, "distanceMeters mut not be null");
    Objects.requireNonNull(estimatedTime, "estimatedTime mut not be null");
    Objects.requireNonNull(expenseInCents, "expenseInCents mut not be null");

    if (expenseInCents < 0) {
      throw new IllegalArgumentException("expenseInCents can not be negative");
    }

    if (distanceMeters < 0) {
      throw new IllegalArgumentException("distanceMeters can not be negative");
    }
  }
}
