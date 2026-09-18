package com.dev.ds;

import java.util.Objects;

/**
 * A priority queue over a fixed number of integer levels, backed by one FIFO
 * queue per level. Because the number of levels is constant, both enqueue and
 * highest-priority dequeue run in O(1) time.
 *
 * @param <T> the type of elements held in this queue
 */
public class BucketQueue<T> {

  private final Queue<T>[] buckets;
  private final int maxLevel;
  private int size;

  @SuppressWarnings("unchecked")
  public BucketQueue(int maxLevel) {
    if (maxLevel <= 0) {
      throw new IllegalArgumentException("maxLevel must be greater than 0");
    }

    this.maxLevel = maxLevel;
    this.buckets = (Queue<T>[]) new Queue[maxLevel + 1];

    for (int level = 1; level <= maxLevel; level++) {
      buckets[level] = new Queue<>();
    }
  }

  /** Adds an element at the given level (1 is lowest, {@code maxLevel} highest). */
  public void enqueue(T value, int level) {
    Objects.requireNonNull(value, "value must not be null");

    if (level < 1 || level > maxLevel) {
      throw new IllegalArgumentException("level out of range: " + level);
    }

    buckets[level].enqueue(value);
    size++;
  }

  /** Removes and returns the element with the highest level, FIFO within a level. */
  public T dequeueMax() {
    int level = highestLevel();

    if (level == 0) {
      throw new IllegalStateException("Cannot dequeue from an empty bucket queue");
    }

    size--;

    return buckets[level].dequeue();
  }

  /** Returns the element that {@link #dequeueMax()} would remove. */
  public T peekMax() {
    int level = highestLevel();

    if (level == 0) {
      throw new IllegalStateException("Cannot peek an empty bucket queue");
    }

    return buckets[level].peek();
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  public int maxLevel() {
    return maxLevel;
  }

  public void clear() {
    for (int level = 1; level <= maxLevel; level++) {
      buckets[level].clear();
    }
    size = 0;
  }

  private int highestLevel() {
    for (int level = maxLevel; level >= 1; level--) {
      if (!buckets[level].isEmpty()) {
        return level;
      }
    }

    return 0;
  }
}
