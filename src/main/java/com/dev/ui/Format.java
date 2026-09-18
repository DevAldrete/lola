package com.dev.ui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Small presentation helpers shared by the panels. */
public final class Format {

  private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

  public static String dateTime(LocalDateTime value) {
    return value == null ? "-" : value.format(DATE_TIME);
  }

  public static String weight(float kilograms) {
    return String.format("%.1f kg", kilograms);
  }
}
