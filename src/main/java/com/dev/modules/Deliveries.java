package com.dev.modules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Priority;
import com.dev.ds.HashMap;
import com.dev.ds.PriorityQueue;

/**
 * Operations over packages: urgency ordering, waybill lookup and tracking.
 * All functions are stateless and return new data, keeping packages immutable.
 */
public final class Deliveries {

  private Deliveries() {
  }

  /** Orders packages by urgency, breaking ties by id for deterministic results. */
  public static final Comparator<Package> URGENCY = Comparator
      .comparingInt((Package p) -> p.priority().ordinal())
      .thenComparingInt(Package::id);

  /** Loads the given packages into a min-priority queue ordered by urgency. */
  public static PriorityQueue<Package> toPriorityQueue(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    PriorityQueue<Package> queue = new PriorityQueue<>(URGENCY);

    for (Package pkg : packages) {
      queue.enqueue(pkg);
    }

    return queue;
  }

  /** Returns the most urgent packages, at most {@code limit} of them. */
  public static List<Package> urgent(List<Package> packages, int limit) {
    if (limit < 0) {
      throw new IllegalArgumentException("limit must be non negative");
    }

    PriorityQueue<Package> queue = toPriorityQueue(packages);

    List<Package> result = new ArrayList<>(Math.min(limit, queue.size()));

    while (!queue.isEmpty() && result.size() < limit) {
      result.add(queue.dequeue());
    }

    return result;
  }

  /** Builds a waybill-to-package index for constant time lookups. */
  public static HashMap<String, Package> indexByWaybill(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    HashMap<String, Package> index = new HashMap<>(Math.max(packages.size() * 2, 2));

    for (Package pkg : packages) {
      index.put(pkg.waybill(), pkg);
    }

    return index;
  }

  /** Looks up a package by waybill, returning empty when it is not tracked. */
  public static Optional<Package> findByWaybill(HashMap<String, Package> index, String waybill) {
    Objects.requireNonNull(index, "index must not be null");
    Objects.requireNonNull(waybill, "waybill must not be null");

    return Optional.ofNullable(index.get(waybill));
  }

  /** Returns a new list with the matching waybill moved to the given status. */
  public static List<Package> updateStatus(List<Package> packages, String waybill,
      DeliveryStatus status) {
    Objects.requireNonNull(packages, "packages must not be null");
    Objects.requireNonNull(waybill, "waybill must not be null");
    Objects.requireNonNull(status, "status must not be null");

    List<Package> updated = new ArrayList<>(packages.size());

    for (Package pkg : packages) {
      updated.add(pkg.waybill().equals(waybill) ? pkg.withStatus(status) : pkg);
    }

    return updated;
  }

  /** Returns the packages carrying the given status. */
  public static List<Package> filterByStatus(List<Package> packages, DeliveryStatus status) {
    Objects.requireNonNull(packages, "packages must not be null");
    Objects.requireNonNull(status, "status must not be null");

    List<Package> result = new ArrayList<>();

    for (Package pkg : packages) {
      if (pkg.status() == status) {
        result.add(pkg);
      }
    }

    return result;
  }

  /** Sums the weight of every package. */
  public static float totalWeight(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    float total = 0f;

    for (Package pkg : packages) {
      total += pkg.weight();
    }

    return total;
  }

  /** Sums the price of every package, in cents. */
  public static long totalPriceInCents(List<Package> packages) {
    Objects.requireNonNull(packages, "packages must not be null");

    long total = 0L;

    for (Package pkg : packages) {
      total += pkg.priceInCents();
    }

    return total;
  }

  /** Counts the packages with the given priority. */
  public static int countByPriority(List<Package> packages, Priority priority) {
    Objects.requireNonNull(packages, "packages must not be null");
    Objects.requireNonNull(priority, "priority must not be null");

    int count = 0;

    for (Package pkg : packages) {
      if (pkg.priority() == priority) {
        count++;
      }
    }

    return count;
  }
}
