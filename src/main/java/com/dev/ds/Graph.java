package com.dev.ds;

import java.util.ArrayList;
import java.util.List;

public class Graph<V> {

  private final HashMap<V, List<V>> adjacencyList;

  public Graph(int capacity) {
    adjacencyList = new HashMap<>(capacity);
  }

  public void addVertex(V vertex) {
    if (!adjacencyList.containsKey(vertex)) {
      adjacencyList.put(vertex, new ArrayList<>());
    }
  }

  public void addEdge(V from, V to) {
    addVertex(from);
    addVertex(to);

    adjacencyList.get(from).add(to);
  }

  public List<V> neighbors(V vertex) {
    List<V> neighbors = adjacencyList.get(vertex);

    if (neighbors == null) {
      throw new IllegalArgumentException(
          "Vertex does not exist: " + vertex);
    }

    return neighbors;
  }

  public boolean containsVertex(V vertex) {
    return adjacencyList.containsKey(vertex);
  }

  public int size() {
    return adjacencyList.size();
  }

  public List<V> bfs(V start) {
    if (!containsVertex(start)) {
      throw new IllegalArgumentException("Vertex does not exist: " + start);
    }

    List<V> result = new ArrayList<>();

    HashMap<V, Boolean> visited = new HashMap<>(size());

    Queue<V> queue = new Queue<>();

    queue.enqueue(start);
    visited.put(start, true);

    while (!queue.isEmpty()) {
      V current = queue.dequeue();

      result.add(current);

      for (V neighbor : neighbors(current)) {
        if (!visited.containsKey(neighbor)) {
          visited.put(neighbor, true);
          queue.enqueue(neighbor);
        }
      }
    }

    return result;
  }

  public List<V> dfs(V start) {
    if (!containsVertex(start)) {
      throw new IllegalArgumentException("Vertex does not exist: " + start);
    }

    List<V> result = new ArrayList<>();

    HashMap<V, Boolean> visited = new HashMap<>(size());

    Stack<V> stack = new Stack<>();

    stack.push(start);
    visited.put(start, true);

    while (!stack.isEmpty()) {
      V current = stack.pop();

      result.add(current);

      for (V neighbor : neighbors(current)) {
        if (!visited.containsKey(neighbor)) {
          visited.put(neighbor, true);
          stack.push(neighbor);
        }
      }
    }

    return result;
  }

  public HashMap<V, Integer> bfsDistances(V start) {
    if (!containsVertex(start)) {
      throw new IllegalArgumentException("Vertex does not exist: " + start);
    }

    HashMap<V, Integer> distance = new HashMap<>(size());
    Queue<V> queue = new Queue<>();

    queue.enqueue(start);
    distance.put(start, 0);

    while (!queue.isEmpty()) {
      V current = queue.dequeue();

      int currentDistance = distance.get(current);

      for (V neighbor : neighbors(current)) {
        if (!distance.containsKey(neighbor)) {
          distance.put(neighbor, currentDistance + 1);
          queue.enqueue(neighbor);
        }
      }
    }

    return distance;
  }

  public List<V> shortestPath(V start, V target) {
    if (!containsVertex(start) || !containsVertex(target)) {
      throw new IllegalArgumentException("Vertex does not exist");
    }

    HashMap<V, V> parent = new HashMap<>(size());
    Queue<V> queue = new Queue<>();

    queue.enqueue(start);
    parent.put(start, null);

    while (!queue.isEmpty()) {
      V current = queue.dequeue();

      if (current.equals(target)) {
        break;
      }

      for (V neighbor : neighbors(current)) {
        if (!parent.containsKey(neighbor)) {
          parent.put(neighbor, current);
          queue.enqueue(neighbor);
        }
      }
    }

    if (!parent.containsKey(target)) {
      return new ArrayList<>();
    }

    // Reconstruct backwards
    List<V> path = new ArrayList<>();

    V current = target;

    while (current != null) {
      path.add(current);
      current = parent.get(current);
    }

    // Reverse path
    int left = 0;
    int right = path.size() - 1;

    while (left < right) {
      V temp = path.get(left);
      path.set(left, path.get(right));
      path.set(right, temp);

      left++;
      right--;
    }

    return path;
  }
}
