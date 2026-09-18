package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class WeightedGraphTest {

  private static WeightedGraph<String> sample() {
    WeightedGraph<String> graph = new WeightedGraph<>(8);

    graph.addEdge("A", "B", 1);
    graph.addEdge("A", "C", 5);
    graph.addEdge("B", "C", 1);
    graph.addEdge("C", "D", 1);

    return graph;
  }

  @Test
  void addEdgeRegistersVertices() {
    WeightedGraph<String> graph = sample();

    assertEquals(4, graph.size());
    assertTrue(graph.containsVertex("A"));
    assertTrue(graph.containsVertex("D"));
    assertEquals(2, graph.neighbors("A").size());
  }

  @Test
  void shortestPathMinimizesTotalWeightNotHops() {
    WeightedGraph.Path<String> path = sample().shortestPath("A", "D").orElseThrow();

    assertEquals(List.of("A", "B", "C", "D"), path.vertices());
    assertEquals(3.0, path.weight(), 0.0001);
  }

  @Test
  void shortestPathToSelfIsZero() {
    WeightedGraph.Path<String> path = sample().shortestPath("A", "A").orElseThrow();

    assertEquals(List.of("A"), path.vertices());
    assertEquals(0.0, path.weight(), 0.0001);
  }

  @Test
  void unreachableTargetReturnsEmpty() {
    WeightedGraph<String> graph = sample();
    graph.addVertex("Island");

    assertTrue(graph.shortestPath("A", "Island").isEmpty());
  }

  @Test
  void rejectsUnknownVerticesAndNegativeWeights() {
    WeightedGraph<String> graph = sample();

    assertThrows(IllegalArgumentException.class, () -> graph.neighbors("Missing"));
    assertThrows(IllegalArgumentException.class, () -> graph.shortestPath("A", "Missing"));
    assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", "B", -1));
  }

  @Test
  void edgeExposesTargetAndWeight() {
    WeightedGraph.Edge<String> edge = sample().neighbors("A").get(0);

    assertEquals("B", edge.to());
    assertEquals(1.0, edge.weight(), 0.0001);
    assertFalse(edge.to().isBlank());
  }
}
