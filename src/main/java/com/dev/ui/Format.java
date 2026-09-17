package com.dev.ui;

import java.time.Duration;

/** Small presentation helpers shared by the panels. */
public final class Format {

  private Format() {
  }

  public static String money(long cents) {
    return String.format("R$ %.2f", cents / 100.0);
  }

  public static String distance(double meters) {
    return meters >= 1_000
        ? String.format("%.1f km", meters / 1_000)
        : String.format("%.0f m", meters);
  }

  public static String duration(Duration duration) {
    long hours = duration.toHours();

    return hours > 0
        ? hours + "h " + duration.toMinutesPart() + "min"
        : duration.toMinutes() + "min";
  }
}
