package com.dev.modules;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.dev.domain.Route;
import com.dev.domain.Zone;
import com.dev.ds.Graph;
import com.dev.ds.HashMap;

/**
 * Operations over routes and zones: the transportation network, route ordering,
 * cumulative travel time and geographic partitioning.
 */
public final class Routing {

  private Routing() {
  }

  /** Sorts routes from cheapest to most expensive, in cents. */
  public static final Comparator<Route> BY_EXPENSE = Comparator.comparingLong(Route::expenseInCents);

  /** Sorts routes from shortest to longest, in meters. */
  public static final Comparator<Route> BY_DISTANCE = Comparator
      .comparingDouble(Route::distanceMeters);

  /** Sorts routes from fastest to slowest, by estimated time. */
  public static final Comparator<Route> BY_TIME = Comparator.comparing(Route::estimatedTime);

  // ---------------------------------------------------------------------------
  // Graphs: cities and transportation routes
  // ---------------------------------------------------------------------------

  /** Builds a directed graph of zone ids connected by the given routes. */
  public static Graph<Integer> network(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    Graph<Integer> graph = new Graph<>(Math.max(routes.size() * 2, 2));

    for (Route route : routes) {
      graph.addEdge(route.originId(), route.destinyId());
    }

    return graph;
  }

  /** Finds the fewest-stops path between two zones, or an empty list when unreachable. */
  public static List<Integer> shortestPath(Graph<Integer> network, int originId, int destinyId) {
    Objects.requireNonNull(network, "network must not be null");

    return network.shortestPath(originId, destinyId);
  }

  // ---------------------------------------------------------------------------
  // Sorting: routes by distance, time and cost
  // ---------------------------------------------------------------------------

  /** Returns a new list of routes ordered by the given comparator using merge sort. */
  public static List<Route> sortRoutes(List<Route> routes, Comparator<Route> comparator) {
    Objects.requireNonNull(routes, "routes must not be null");
    Objects.requireNonNull(comparator, "comparator must not be null");

    return mergeSort(routes, comparator);
  }

  public static List<Route> sortByExpense(List<Route> routes) {
    return mergeSort(routes, BY_EXPENSE);
  }

  public static List<Route> sortByDistance(List<Route> routes) {
    return mergeSort(routes, BY_DISTANCE);
  }

  public static List<Route> sortByTime(List<Route> routes) {
    return mergeSort(routes, BY_TIME);
  }

  private static <T> List<T> mergeSort(List<T> values, Comparator<? super T> comparator) {
    if (values.size() <= 1) {
      return new ArrayList<>(values);
    }

    int middle = values.size() / 2;

    List<T> left = mergeSort(new ArrayList<>(values.subList(0, middle)), comparator);
    List<T> right = mergeSort(new ArrayList<>(values.subList(middle, values.size())), comparator);

    return merge(left, right, comparator);
  }

  private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> comparator) {
    List<T> result = new ArrayList<>(left.size() + right.size());

    int i = 0;
    int j = 0;

    while (i < left.size() && j < right.size()) {
      if (comparator.compare(left.get(i), right.get(j)) <= 0) {
        result.add(left.get(i++));
      } else {
        result.add(right.get(j++));
      }
    }

    while (i < left.size()) {
      result.add(left.get(i++));
    }

    while (j < right.size()) {
      result.add(right.get(j++));
    }

    return result;
  }

  // ---------------------------------------------------------------------------
  // Recursion: cumulative time on a route chain
  // ---------------------------------------------------------------------------

  /** Sums the estimated time of every route in the chain through recursion. */
  public static Duration cumulativeTime(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    return cumulativeTime(routes, routes.size());
  }

  private static Duration cumulativeTime(List<Route> routes, int count) {
    if (count == 0) {
      return Duration.ZERO;
    }

    return routes.get(count - 1).estimatedTime().plus(cumulativeTime(routes, count - 1));
  }

  // ---------------------------------------------------------------------------
  // Divide and conquer: partitioning zones
  // ---------------------------------------------------------------------------

  /** Groups zones by state by recursively splitting and merging the list. */
  public static HashMap<String, List<Zone>> partitionByState(List<Zone> zones) {
    Objects.requireNonNull(zones, "zones must not be null");

    return partitionByState(zones, 0, zones.size());
  }

  private static HashMap<String, List<Zone>> partitionByState(List<Zone> zones, int from, int to) {
    if (to - from <= 1) {
      HashMap<String, List<Zone>> single = new HashMap<>(2);

      if (to - from == 1) {
        single.put(zones.get(from).state(), new ArrayList<>(List.of(zones.get(from))));
      }

      return single;
    }

    int middle = from + (to - from) / 2;

    HashMap<String, List<Zone>> left = partitionByState(zones, from, middle);
    HashMap<String, List<Zone>> right = partitionByState(zones, middle, to);

    return merge(left, right);
  }

  private static HashMap<String, List<Zone>> merge(HashMap<String, List<Zone>> left,
      HashMap<String, List<Zone>> right) {
    HashMap<String, List<Zone>> merged = new HashMap<>(Math.max((left.size() + right.size()) * 2, 2));

    for (String state : left.keys()) {
      merged.put(state, new ArrayList<>(left.get(state)));
    }

    for (String state : right.keys()) {
      List<Zone> existing = merged.get(state);

      if (existing == null) {
        merged.put(state, new ArrayList<>(right.get(state)));
      } else {
        existing.addAll(right.get(state));
      }
    }

    return merged;
  }
}
