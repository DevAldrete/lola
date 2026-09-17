package com.dev.ds;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T extends Comparable<? super T>> {
  public static enum Order {
    PREORDER,
    INORDER,
    POSTORDER
  }

  private static class Node<E> {
    E value;
    Node<E> left;
    Node<E> right;

    public Node(E value) {
      this.value = value;
      this.left = null;
      this.right = null;
    }
  }

  private Node<T> root;
  private int size;

  public boolean isEmpty() {
    return root == null || size == 0;
  }

  public int size() {
    return size;
  }

  public void push(T value) {
    if (isEmpty()) {
      size++;
      root = new Node<T>(value);
    }

    Node<T> curr = root;
    int cmp = 0;

    while (curr != null) {
      cmp = curr.value.compareTo(value);
      if (cmp < 0) {
        if (curr.left == null) {
          curr.left = new Node<T>(value);
          size++;
          break;
        }
        curr = curr.left;
      } else if (cmp > 0) {
        if (curr.right == null) {
          curr.right = new Node<T>(value);
          size++;
          break;
        }
        curr = curr.right;
      } else {
        return;
      }
    }
  }

  private List<T> preOrder() {
    // TODO implement pre order walk
    return null;
  }

  private List<T> inOrder() {
    // TODO implement in order walk
    return null;
  }

  private List<T> postOrder() {
    // TODO implement post order walk
    return null;
  }

  public List<T> asList(Order order) {
    List<T> result = new ArrayList<T>();

    switch (order) {
      case PREORDER:
        // TODO Implement preorder walk
        break;
      case INORDER:
        // TODO Implement inorder walk
        break;
      case POSTORDER:
        // TODO Implement postorder walk
        break;
    }

    return result;
  }

  private void rshift(Node<T> node) {
    Node<T> curr = node;
    Node<T> right = curr.right;
    Node<T> left = curr.left;

    if (right == null) {
      while (curr != null) {
        curr = curr.left;
      }
      size--;
      return;
    }

    Node<T> tmp = null;

    while (curr != null) {
      tmp = right.left;
      right.left = left;
      left = tmp;
      curr = right;
      right = curr.right;
    }

    size--;
  }

  public T pop() {
    if (isEmpty())
      return null;

    T val = root.value;

    rshift(root);

    return val;
  }

  public boolean delete(T value) {
    if (isEmpty())
      return false;

    Node<T> curr = root;
    int cmp = curr.value.compareTo(value);

    if (cmp == 0) {
      pop();
    }

    while (curr != null) {
      cmp = curr.value.compareTo(value);
      if (cmp < 0) {
        curr = curr.left;
      } else if (cmp > 0) {
        curr = curr.right;
      } else {
        rshift(curr);
        return true;
      }
    }

    return false;
  }
}
