package com.dev.modules;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.domain.Vehicle;
import com.dev.domain.Zone;
import com.dev.ds.Graph;
import com.dev.ds.HashMap;
import com.dev.ds.Sorting;
import com.dev.ds.WeightedGraph;

/**
 * Operations over routes, zones and the delivery fleet: the transportation
 * network, route ordering, cumulative travel analysis, weighted shortest paths
 * and geographic partitioning.
 */
public final class Routing {

  private Routing() {
  }

  /** Metric used to weight the edges of the transportation network. */
  public enum RouteWeight {
    TIME,
    COST,
    DISTANCE
  }

  /** A load assigned to a vehicle by the partitioning algorithm. */
  public record Assignment(Vehicle vehicle, List<Package> packages, float totalWeight) {
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

  /** Builds an unweighted directed graph of zone ids connected by the routes. */
  public static Graph<Integer> network(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    Graph<Integer> graph = new Graph<>(Math.max(routes.size() * 2, 2));

    for (Route route : routes) {
      graph.addEdge(route.originId(), route.destinyId());
    }

    return graph;
  }

  /** Finds the fewest-stops path between two zones, or empty when unreachable. */
  public static List<Integer> shortestPath(Graph<Integer> network, int originId, int destinyId) {
    Objects.requireNonNull(network, "network must not be null");

    return network.shortestPath(originId, destinyId);
  }

  /** Builds a directed, weighted graph using the chosen metric. */
  public static WeightedGraph<Integer> weightedNetwork(List<Route> routes, RouteWeight metric) {
    Objects.requireNonNull(routes, "routes must not be null");
    Objects.requireNonNull(metric, "metric must not be null");

    WeightedGraph<Integer> graph = new WeightedGraph<>(Math.max(routes.size() * 2, 2));

    for (Route route : routes) {
      graph.addEdge(route.originId(), route.destinyId(), weightOf(route, metric));
    }

    return graph;
  }

  /** Finds the cheapest-weight path between two zones, or empty when unreachable. */
  public static Optional<WeightedGraph.Path<Integer>> weightedShortestPath(
      WeightedGraph<Integer> network, int originId, int destinyId) {
    Objects.requireNonNull(network, "network must not be null");

    return network.shortestPath(originId, destinyId);
  }

  private static double weightOf(Route route, RouteWeight metric) {
    return switch (metric) {
      case TIME -> route.estimatedTime().toSeconds();
      case COST -> route.expenseInCents();
      case DISTANCE -> route.distanceMeters();
    };
  }

  // ---------------------------------------------------------------------------
  // Sorting: routes by distance, time and cost
  // ---------------------------------------------------------------------------

  /** Returns a new list of routes ordered by the given comparator. */
  public static List<Route> sortRoutes(List<Route> routes, Comparator<Route> comparator) {
    return Sorting.mergeSort(routes, comparator);
  }

  public static List<Route> sortByExpense(List<Route> routes) {
    return Sorting.mergeSort(routes, BY_EXPENSE);
  }

  public static List<Route> sortByDistance(List<Route> routes) {
    return Sorting.mergeSort(routes, BY_DISTANCE);
  }

  public static List<Route> sortByTime(List<Route> routes) {
    return Sorting.mergeSort(routes, BY_TIME);
  }

  // ---------------------------------------------------------------------------
  // Recursion: cumulative time, cost and distance on a route chain
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

  /** Sums the expense of every route in the chain, in cents, through recursion. */
  public static long cumulativeCost(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    return cumulativeCost(routes, routes.size());
  }

  private static long cumulativeCost(List<Route> routes, int count) {
    if (count == 0) {
      return 0L;
    }

    return routes.get(count - 1).expenseInCents() + cumulativeCost(routes, count - 1);
  }

  /** Sums the distance of every route in the chain, in meters, through recursion. */
  public static double cumulativeDistance(List<Route> routes) {
    Objects.requireNonNull(routes, "routes must not be null");

    return cumulativeDistance(routes, routes.size());
  }

  private static double cumulativeDistance(List<Route> routes, int count) {
    if (count == 0) {
      return 0d;
    }

    return routes.get(count - 1).distanceMeters() + cumulativeDistance(routes, count - 1);
  }

  // ---------------------------------------------------------------------------
  // Divide and conquer: geographic partitioning
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

  /**
   * Splits the batch into at most one segment per vehicle and pairs the
   * heaviest segment with the largest vehicle. Returns an empty list when
   * there is nothing to assign.
   */
  public static List<Assignment> partitionDeliveries(List<Package> packages, List<Vehicle> vehicles) {
    Objects.requireNonNull(packages, "packages must not be null");
    Objects.requireNonNull(vehicles, "vehicles must not be null");

    if (packages.isEmpty() || vehicles.isEmpty()) {
      return List.of();
    }

    List<List<Package>> segments = new ArrayList<>();

    for (List<Package> segment : divide(new ArrayList<>(packages), vehicles.size())) {
      if (!segment.isEmpty()) {
        segments.add(segment);
      }
    }

    List<Vehicle> orderedVehicles = Sorting.mergeSort(vehicles,
        Comparator.comparingDouble(Vehicle::capacityKg).reversed());
    List<List<Package>> orderedSegments = Sorting.mergeSort(segments,
        Comparator.comparingDouble(Routing::segmentWeight).reversed());

    List<Assignment> assignments = new ArrayList<>();
    int count = Math.min(orderedVehicles.size(), orderedSegments.size());

    for (int i = 0; i < count; i++) {
      List<Package> segment = orderedSegments.get(i);
      assignments.add(new Assignment(orderedVehicles.get(i), segment, (float) segmentWeight(segment)));
    }

    return assignments;
  }

  private static List<List<Package>> divide(List<Package> packages, int parts) {
    if (parts <= 1 || packages.size() <= 1) {
      List<List<Package>> single = new ArrayList<>();
      single.add(new ArrayList<>(packages));
      return single;
    }

    int middle = packages.size() / 2;
    int leftParts = parts / 2;

    List<List<Package>> segments = new ArrayList<>();
    segments.addAll(divide(packages.subList(0, middle), leftParts));
    segments.addAll(divide(packages.subList(middle, packages.size()), parts - leftParts));

    return segments;
  }

  private static double segmentWeight(List<Package> packages) {
    double total = 0d;

    for (Package pkg : packages) {
      total += pkg.weight();
    }

    return total;
  }
}
