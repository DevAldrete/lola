package com.dev.domain;

import java.util.Objects;

/**
 * A node of the distribution hierarchy (national, regional or local).
 *
 * @param parentId id of the parent center, or 0 for the national root
 */
public record DistributionCenter(int id, String name, CenterLevel level, int parentId, int zoneId) {

  public DistributionCenter {
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(level, "level must not be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
  }

  public boolean isRoot() {
    return parentId == 0;
  }
}
