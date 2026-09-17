package com.dev.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A generic binary search tree. Values are kept unique and ordered so that
 * every left child is smaller and every right child is greater than its parent.
 *
 * @param <T> the type of elements held in this tree
 */
public class BinaryTree<T extends Comparable<? super T>> {

  public enum Order {
    PREORDER,
    INORDER,
    POSTORDER
  }

  private static class Node<E> {
    E value;
    Node<E> left;
    Node<E> right;

    Node(E value) {
      this.value = value;
    }
  }

  private Node<T> root;
  private int size;

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  /** Inserts a value, ignoring values already present in the tree. */
  public void push(T value) {
    Objects.requireNonNull(value, "value must not be null");

    if (isEmpty()) {
      root = new Node<>(value);
      size++;
      return;
    }

    Node<T> current = root;

    while (true) {
      int cmp = value.compareTo(current.value);

      if (cmp < 0) {
        if (current.left == null) {
          current.left = new Node<>(value);
          size++;
          return;
        }
        current = current.left;
      } else if (cmp > 0) {
        if (current.right == null) {
          current.right = new Node<>(value);
          size++;
          return;
        }
        current = current.right;
      } else {
        return;
      }
    }
  }

  /** Returns true if the value is present in the tree. */
  public boolean contains(T value) {
    Objects.requireNonNull(value, "value must not be null");

    Node<T> current = root;

    while (current != null) {
      int cmp = value.compareTo(current.value);

      if (cmp < 0) {
        current = current.left;
      } else if (cmp > 0) {
        current = current.right;
      } else {
        return true;
      }
    }

    return false;
  }

  /** Removes and returns the value at the root, or null when the tree is empty. */
  public T pop() {
    if (isEmpty()) {
      return null;
    }

    T value = root.value;
    root = remove(root, value);
    size--;

    return value;
  }

  /** Removes the given value, returning false when it is not present. */
  public boolean delete(T value) {
    Objects.requireNonNull(value, "value must not be null");

    if (!contains(value)) {
      return false;
    }

    root = remove(root, value);
    size--;

    return true;
  }

  private Node<T> remove(Node<T> node, T value) {
    if (node == null) {
      return null;
    }

    int cmp = value.compareTo(node.value);

    if (cmp < 0) {
      node.left = remove(node.left, value);
    } else if (cmp > 0) {
      node.right = remove(node.right, value);
    } else {
      if (node.left == null) {
        return node.right;
      }
      if (node.right == null) {
        return node.left;
      }

      // Two children: promote the in-order successor, then drop it from below.
      Node<T> successor = min(node.right);
      node.value = successor.value;
      node.right = remove(node.right, successor.value);
    }

    return node;
  }

  private Node<T> min(Node<T> node) {
    Node<T> current = node;
    while (current.left != null) {
      current = current.left;
    }
    return current;
  }

  /** Returns the values in the requested traversal order. */
  public List<T> asList(Order order) {
    Objects.requireNonNull(order, "order must not be null");

    List<T> result = new ArrayList<>(size);

    switch (order) {
      case PREORDER -> preOrder(root, result);
      case INORDER -> inOrder(root, result);
      case POSTORDER -> postOrder(root, result);
    }

    return result;
  }

  private void preOrder(Node<T> node, List<T> result) {
    if (node == null) {
      return;
    }

    result.add(node.value);
    preOrder(node.left, result);
    preOrder(node.right, result);
  }

  private void inOrder(Node<T> node, List<T> result) {
    if (node == null) {
      return;
    }

    inOrder(node.left, result);
    result.add(node.value);
    inOrder(node.right, result);
  }

  private void postOrder(Node<T> node, List<T> result) {
    if (node == null) {
      return;
    }

    postOrder(node.left, result);
    postOrder(node.right, result);
    result.add(node.value);
  }
}
