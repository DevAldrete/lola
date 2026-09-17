package com.dev.ds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * A generic min-priority queue backed by a binary heap stored in an array.
 * The element considered "smallest" by the supplied comparator is dequeued
 * first. Core operations run in O(log n) time.
 *
 * @param <T> the type of elements held in this queue
 */
public class PriorityQueue<T> {

  private static final int DEFAULT_CAPACITY = 11;

  private Object[] heap;
  private int size;
  private final Comparator<? super T> comparator;

  public PriorityQueue(Comparator<? super T> comparator) {
    this(DEFAULT_CAPACITY, comparator);
  }

  public PriorityQueue(int capacity, Comparator<? super T> comparator) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0");
    }

    this.heap = new Object[capacity];
    this.size = 0;
    this.comparator = Objects.requireNonNull(comparator, "comparator must not be null");
  }

  /** Adds an element to the queue. */
  public void enqueue(T value) {
    Objects.requireNonNull(value, "value must not be null");

    if (size == heap.length) {
      heap = Arrays.copyOf(heap, heap.length * 2);
    }

    heap[size] = value;
    siftUp(size);
    size++;
  }

  /** Removes and returns the element with the highest priority. */
  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty priority queue");
    }

    T highest = valueAt(0);

    size--;
    if (size > 0) {
      heap[0] = heap[size];
      siftDown(0);
    }
    heap[size] = null;

    return highest;
  }

  /** Returns the element with the highest priority without removing it. */
  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek an empty priority queue");
    }

    return valueAt(0);
  }

  /** Returns true if the queue has no elements. */
  public boolean isEmpty() {
    return size == 0;
  }

  /** Returns the number of elements currently in the queue. */
  public int size() {
    return size;
  }

  /** Removes all elements from the queue. */
  public void clear() {
    Arrays.fill(heap, null);
    size = 0;
  }

  private void siftUp(int index) {
    T value = valueAt(index);

    while (index > 0) {
      int parent = (index - 1) / 2;

      if (comparator.compare(value, valueAt(parent)) >= 0) {
        break;
      }

      heap[index] = heap[parent];
      index = parent;
    }

    heap[index] = value;
  }

  private void siftDown(int index) {
    T value = valueAt(index);
    int half = size / 2;

    while (index < half) {
      int left = index * 2 + 1;
      int smallest = left;

      int right = left + 1;
      if (right < size && comparator.compare(valueAt(right), valueAt(left)) < 0) {
        smallest = right;
      }

      if (comparator.compare(valueAt(smallest), value) >= 0) {
        break;
      }

      heap[index] = heap[smallest];
      index = smallest;
    }

    heap[index] = value;
  }

  @SuppressWarnings("unchecked")
  private T valueAt(int index) {
    return (T) heap[index];
  }
}
