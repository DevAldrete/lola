package com.dev.modules;

import static com.dev.lola.Fixtures.route;
import static com.dev.lola.Fixtures.zone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dev.domain.Route;
import com.dev.domain.Zone;
import com.dev.ds.Graph;
import com.dev.ds.HashMap;

class RoutingTest {

  private static List<Route> sampleRoutes() {
    return List.of(
        route(1, 10, 20, 300, Duration.ofMinutes(30), 900),
        route(2, 20, 30, 100, Duration.ofMinutes(10), 500),
        route(3, 10, 30, 200, Duration.ofMinutes(20), 300),
        route(4, 30, 40, 100, Duration.ofMinutes(50), 100));
  }

  private static List<Integer> ids(List<Route> routes) {
    return routes.stream().map(Route::id).toList();
  }

  @Test
  void networkConnectsOriginsAndDestinations() {
    Graph<Integer> network = Routing.network(sampleRoutes());

    assertEquals(4, network.size());
    assertTrue(network.containsVertex(10));
    assertTrue(network.containsVertex(40));
    assertTrue(network.neighbors(10).contains(20));
    assertTrue(network.neighbors(10).contains(30));
  }

  @Test
  void shortestPathFindsFewestStops() {
    Graph<Integer> network = Routing.network(sampleRoutes());

    assertEquals(List.of(10, 30, 40), Routing.shortestPath(network, 10, 40));
  }

  @Test
  void shortestPathReturnsEmptyWhenUnreachable() {
    Graph<Integer> network = Routing.network(sampleRoutes());

    assertTrue(Routing.shortestPath(network, 40, 10).isEmpty());
  }

  @Test
  void sortsRoutesByExpense() {
    assertEquals(List.of(4, 3, 2, 1), ids(Routing.sortByExpense(sampleRoutes())));
  }

  @Test
  void sortsRoutesByDistanceStably() {
    assertEquals(List.of(2, 4, 3, 1), ids(Routing.sortByDistance(sampleRoutes())));
  }

  @Test
  void sortsRoutesByTime() {
    assertEquals(List.of(2, 3, 1, 4), ids(Routing.sortByTime(sampleRoutes())));
  }

  @Test
  void sortingDoesNotMutateInput() {
    List<Route> routes = sampleRoutes();

    Routing.sortByExpense(routes);

    assertEquals(List.of(1, 2, 3, 4), ids(routes));
  }

  @Test
  void cumulativeTimeSumsEveryRoute() {
    assertEquals(Duration.ofMinutes(110), Routing.cumulativeTime(sampleRoutes()));
  }

  @Test
  void cumulativeTimeOfEmptyChainIsZero() {
    assertEquals(Duration.ZERO, Routing.cumulativeTime(List.of()));
  }

  @Test
  void partitionsZonesByState() {
    List<Zone> zones = List.of(
        zone(1, "SP", "Campinas"),
        zone(2, "SP", "Santos"),
        zone(3, "RJ", "Rio de Janeiro"),
        zone(4, "MG", "Belo Horizonte"),
        zone(5, "SP", "Osasco"));

    HashMap<String, List<Zone>> partitions = Routing.partitionByState(zones);

    assertEquals(3, partitions.keys().size());
    assertEquals(3, partitions.get("SP").size());
    assertEquals(1, partitions.get("RJ").size());
    assertEquals(1, partitions.get("MG").size());
  }

  @Test
  void partitionsHandleEmptyInput() {
    assertTrue(Routing.partitionByState(List.of()).keys().isEmpty());
  }
}
