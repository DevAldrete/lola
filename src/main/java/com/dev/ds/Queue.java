package com.dev.ds;

public class Queue<T> {

  /**
   * Internal singly linked node holding a single element and a reference
   * to the next node in the chain.
   */
  private static final class Node<E> {
    private E value;
    private Node<E> next;

    Node(E value) {
      this.value = value;
    }
  }

  /** Reference to the front node (next element to be removed). */
  private Node<T> head;

  /** Reference to the rear node (most recently enqueued element). */
  private Node<T> tail;

  /** Number of elements currently stored in the queue. */
  private int size;

  /**
   * Creates an empty queue.
   */
  public Queue() {
    this.head = null;
    this.tail = null;
    this.size = 0;
  }

  /**
   * Adds an element to the rear of the queue.
   *
   * @param value the element to add; may be {@code null}
   */
  public void enqueue(T value) {
    Node<T> node = new Node<>(value);
    if (tail == null) {
      // Queue was empty: the new node is both head and tail.
      head = node;
      tail = node;
    } else {
      tail.next = node;
      tail = node;
    }
    size++;
  }

  /**
   * Removes and returns the element at the front of the queue.
   *
   * @return the element that was at the front of the queue
   * @throws IllegalStateException if the queue is empty
   */
  public T dequeue() {
    if (head == null) {
      throw new IllegalStateException("Cannot dequeue from an empty queue");
    }
    T value = head.value;
    head = head.next;
    if (head == null) {
      // Queue is now empty: tail must also be reset.
      tail = null;
    }
    size--;
    return value;
  }

  /**
   * Returns, but does not remove, the element at the front of the queue.
   *
   * @return the element at the front of the queue
   * @throws IllegalStateException if the queue is empty
   */
  public T peek() {
    if (head == null) {
      throw new IllegalStateException("Cannot peek an empty queue");
    }
    return head.value;
  }

  /**
   * Returns whether the queue contains no elements.
   *
   * @return {@code true} if the queue is empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Returns the number of elements in the queue.
   *
   * @return the current size of the queue
   */
  public int size() {
    return size;
  }

  /**
   * Removes all elements from the queue, leaving it empty.
   */
  public void clear() {
    head = null;
    tail = null;
    size = 0;
  }
}
