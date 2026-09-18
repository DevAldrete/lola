package com.dev.ds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A directed, weighted graph. Weights must be non-negative, which allows
 * {@link #shortestPath(Object, Object)} to use Dijkstra's algorithm driven by
 * our own {@link PriorityQueue}.
 *
 * @param <V> the type of the vertices
 */
public class WeightedGraph<V> {

  public record Edge<V>(V to, double weight) {
  }

  public record Path<V>(List<V> vertices, double weight) {
  }

  private record Entry<V>(V vertex, double distance) {
  }

  private final HashMap<V, List<Edge<V>>> adjacency;

  public WeightedGraph(int capacity) {
    this.adjacency = new HashMap<>(capacity);
  }

  public void addVertex(V vertex) {
    if (!adjacency.containsKey(vertex)) {
      adjacency.put(vertex, new ArrayList<>());
    }
  }

  public void addEdge(V from, V to, double weight) {
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");

    if (weight < 0) {
      throw new IllegalArgumentException("weight must be non negative");
    }

    addVertex(from);
    addVertex(to);

    adjacency.get(from).add(new Edge<>(to, weight));
  }

  public List<Edge<V>> neighbors(V vertex) {
    List<Edge<V>> edges = adjacency.get(vertex);

    if (edges == null) {
      throw new IllegalArgumentException("Vertex does not exist: " + vertex);
    }

    return edges;
  }

  public boolean containsVertex(V vertex) {
    return adjacency.containsKey(vertex);
  }

  public int size() {
    return adjacency.size();
  }

  /** Cheapest path from start to target, or empty when unreachable. */
  public Optional<Path<V>> shortestPath(V start, V target) {
    if (!containsVertex(start) || !containsVertex(target)) {
      throw new IllegalArgumentException("Vertex does not exist");
    }

    HashMap<V, Double> distance = new HashMap<>(size());
    HashMap<V, V> parent = new HashMap<>(size());
    HashMap<V, Boolean> visited = new HashMap<>(size());

    PriorityQueue<Entry<V>> queue = new PriorityQueue<>(Comparator.comparingDouble(Entry::distance));

    distance.put(start, 0.0);
    parent.put(start, null);
    queue.enqueue(new Entry<>(start, 0.0));

    while (!queue.isEmpty()) {
      Entry<V> entry = queue.dequeue();

      if (visited.containsKey(entry.vertex())) {
        continue;
      }

      visited.put(entry.vertex(), true);

      if (entry.vertex().equals(target)) {
        break;
      }

      for (Edge<V> edge : neighbors(entry.vertex())) {
        double candidate = entry.distance() + edge.weight();
        Double known = distance.get(edge.to());

        if (known == null || candidate < known) {
          distance.put(edge.to(), candidate);
          parent.put(edge.to(), entry.vertex());
          queue.enqueue(new Entry<>(edge.to(), candidate));
        }
      }
    }

    if (!parent.containsKey(target)) {
      return Optional.empty();
    }

    List<V> vertices = new ArrayList<>();
    V current = target;

    while (current != null) {
      vertices.add(current);
      current = parent.get(current);
    }

    int left = 0;
    int right = vertices.size() - 1;

    while (left < right) {
      V temporary = vertices.get(left);
      vertices.set(left, vertices.get(right));
      vertices.set(right, temporary);

      left++;
      right--;
    }

    return Optional.of(new Path<>(vertices, distance.get(target)));
  }
}
