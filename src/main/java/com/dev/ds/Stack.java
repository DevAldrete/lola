package com.dev.ds;

/**
 * A simple generic stack (LIFO) implementation backed by a singly linked list.
 * All core operations run in O(1) time.
 *
 * @param <T> the type of elements held in this stack
 */
public class Stack<T> {

  /** Internal node used to build the linked list. */
  private static class Node<T> {
    final T value;
    Node<T> next;

    Node(T value, Node<T> next) {
      this.value = value;
      this.next = next;
    }
  }

  private Node<T> top;
  private int size;

  public Stack() {
    this.top = null;
    this.size = 0;
  }

  /** Pushes an element onto the top of the stack. */
  public void push(T value) {
    top = new Node<>(value, top);
    size++;
  }

  /** Removes and returns the element at the top of the stack. */
  public T pop() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot pop from an empty stack");
    }
    T value = top.value;
    top = top.next;
    size--;
    return value;
  }

  /** Returns the element at the top of the stack without removing it. */
  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek an empty stack");
    }
    return top.value;
  }

  /** Returns true if the stack has no elements. */
  public boolean isEmpty() {
    return size == 0;
  }

  /** Returns the number of elements currently in the stack. */
  public int size() {
    return size;
  }

  /** Removes all elements from the stack. */
  public void clear() {
    top = null;
    size = 0;
  }

  /** Returns true if the given value exists anywhere in the stack. */
  public boolean contains(T value) {
    Node<T> current = top;
    while (current != null) {
      if (current.value == null ? value == null : current.value.equals(value)) {
        return true;
      }
      current = current.next;
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Stack[top -> ");
    Node<T> current = top;
    while (current != null) {
      sb.append(current.value);
      if (current.next != null) {
        sb.append(", ");
      }
      current = current.next;
    }
    sb.append("]");
    return sb.toString();
  }
}
