package com.dev.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A generic n-ary tree. Children keep insertion order, which makes recursive
 * traversals deterministic.
 *
 * @param <T> the type of elements held in this tree
 */
public class Tree<T> {

  public static final class Node<T> {
    private final T value;
    private final List<Node<T>> children = new ArrayList<>();

    Node(T value) {
      this.value = value;
    }

    public T value() {
      return value;
    }

    public List<Node<T>> children() {
      return List.copyOf(children);
    }
  }

  private Node<T> root;
  private int size;

  public void setRoot(T value) {
    Objects.requireNonNull(value, "value must not be null");

    this.root = new Node<>(value);
    this.size = 1;
  }

  public Node<T> root() {
    if (root == null) {
      throw new IllegalStateException("Tree has no root");
    }

    return root;
  }

  public boolean isEmpty() {
    return root == null;
  }

  public int size() {
    return size;
  }

  /** Appends a child under the given parent node. */
  public Node<T> addChild(Node<T> parent, T value) {
    Objects.requireNonNull(parent, "parent must not be null");
    Objects.requireNonNull(value, "value must not be null");

    Node<T> node = new Node<>(value);
    parent.children.add(node);
    size++;

    return node;
  }

  /** Appends a child under the node whose value equals {@code parentValue}. */
  public Node<T> addChild(T parentValue, T value) {
    Node<T> parent = find(parentValue)
        .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + parentValue));

    return addChild(parent, value);
  }

  public Optional<Node<T>> find(T value) {
    Objects.requireNonNull(value, "value must not be null");

    return root == null ? Optional.empty() : find(root, value);
  }

  private Optional<Node<T>> find(Node<T> node, T value) {
    if (node.value.equals(value)) {
      return Optional.of(node);
    }

    for (Node<T> child : node.children) {
      Optional<Node<T>> found = find(child, value);

      if (found.isPresent()) {
        return found;
      }
    }

    return Optional.empty();
  }

  /** Pre-order (parent before children) recursive walk. */
  public List<T> preOrder() {
    List<T> result = new ArrayList<>(size);

    if (root != null) {
      preOrder(root, result);
    }

    return result;
  }

  private void preOrder(Node<T> node, List<T> result) {
    result.add(node.value);

    for (Node<T> child : node.children) {
      preOrder(child, result);
    }
  }

  /** Breadth-first walk grouped by depth level. */
  public List<List<T>> levels() {
    List<List<T>> result = new ArrayList<>();

    if (root == null) {
      return result;
    }

    Queue<Node<T>> queue = new Queue<>();
    queue.enqueue(root);

    while (!queue.isEmpty()) {
      int count = queue.size();
      List<T> level = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
        Node<T> node = queue.dequeue();
        level.add(node.value);

        for (Node<T> child : node.children) {
          queue.enqueue(child);
        }
      }

      result.add(level);
    }

    return result;
  }
}
