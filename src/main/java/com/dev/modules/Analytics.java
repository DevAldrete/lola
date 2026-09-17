package com.dev.modules;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.ds.HashMap;

/**
 * Aggregate metrics over routes and packages: costs, distances and revenue.
 * All functions are stateless and never mutate their inputs.
 */
public final class Analytics {

  private Analytics() {
  }

  // ---------------------------------------------------------------------------
  // Routes
  // ---------------------------------------------------------------------------

  /** Sums the expense of every route, in cents. */
  public static long totalExpense(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    long total = 0L;

    for (Route route : routes) {
      total += route.expenseInCents();
    }

    return total;
  }

  /** Averages the route expense, in cents, or empty when there are no routes. */
  public static OptionalDouble averageExpense(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    if (routes.isEmpty()) {
      return OptionalDouble.empty();
    }

    return OptionalDouble.of((double) totalExpense(routes) / routes.size());
  }

  /** Returns the route with the highest expense, or empty when there are none. */
  public static Optional<Route> mostExpensiveRoute(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    return routes.stream().max((a, b) -> Long.compare(a.expenseInCents(), b.expenseInCents()));
  }

  /** Returns the route with the lowest expense, or empty when there are none. */
  public static Optional<Route> cheapestRoute(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    return routes.stream().min((a, b) -> Long.compare(a.expenseInCents(), b.expenseInCents()));
  }

  /** Sums the distance of every route, in meters. */
  public static double totalDistance(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    double total = 0d;

    for (Route route : routes) {
      total += route.distanceMeters();
    }

    return total;
  }

  // ---------------------------------------------------------------------------
  // Packages
  // ---------------------------------------------------------------------------

  /** Sums the price of every package, in cents. */
  public static long revenueInCents(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    long total = 0L;

    for (Package pkg : packages) {
      total += pkg.priceInCents();
    }

    return total;
  }

  /** Averages the package price, in cents, or empty when there are no packages. */
  public static OptionalDouble averagePriceInCents(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    if (packages.isEmpty()) {
      return OptionalDouble.empty();
    }

    return OptionalDouble.of((double) revenueInCents(packages) / packages.size());
  }

  /** Returns the heaviest package, or empty when there are none. */
  public static Optional<Package> heaviestPackage(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    return packages.stream().max((a, b) -> Float.compare(a.weight(), b.weight()));
  }

  /** Counts packages per delivery status. */
  public static HashMap<DeliveryStatus, Integer> countByStatus(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    HashMap<DeliveryStatus, Integer> counts = new HashMap<>(Math.max(packages.size() * 2, 2));

    for (Package pkg : packages) {
      Integer current = counts.get(pkg.status());
      counts.put(pkg.status(), current == null ? 1 : current + 1);
    }

    return counts;
  }

  /** Sums the revenue per route id, in cents. */
  public static HashMap<Integer, Long> revenueByRoute(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    HashMap<Integer, Long> revenue = new HashMap<>(Math.max(packages.size() * 2, 2));

    for (Package pkg : packages) {
      Long current = revenue.get(pkg.routeId());
      revenue.put(pkg.routeId(), current == null ? pkg.priceInCents() : current + pkg.priceInCents());
    }

    return revenue;
  }
}
