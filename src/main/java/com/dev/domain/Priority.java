package com.dev.domain;

/**
 * Shipment urgency on a 1 (normal) to 5 (critical emergency) scale.
 */
public enum Priority {
  NORMAL(1),
  MODERATE(2),
  IMPORTANT(3),
  URGENT(4),
  CRITICAL(5);

  private final int level;

  Priority(int level) {
    this.level = level;
  }

  /** Numeric urgency, where 5 is the highest priority. */
  public int level() {
    return level;
  }
}
