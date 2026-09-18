package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BucketQueueTest {

  private static BucketQueue<String> sample() {
    BucketQueue<String> queue = new BucketQueue<>(5);

    queue.enqueue("low", 1);
    queue.enqueue("critical-a", 5);
    queue.enqueue("medium", 3);
    queue.enqueue("critical-b", 5);
    queue.enqueue("high", 4);

    return queue;
  }

  @Test
  void dequeuesHighestLevelFirstAndFifoWithinLevel() {
    BucketQueue<String> queue = sample();

    assertEquals("critical-a", queue.dequeueMax());
    assertEquals("critical-b", queue.dequeueMax());
    assertEquals("high", queue.dequeueMax());
    assertEquals("medium", queue.dequeueMax());
    assertEquals("low", queue.dequeueMax());
  }

  @Test
  void peekReturnsHighestWithoutRemoving() {
    BucketQueue<String> queue = sample();

    assertEquals("critical-a", queue.peekMax());
    assertEquals(5, queue.size());
  }

  @Test
  void tracksSizeAndEmptiness() {
    BucketQueue<String> queue = sample();

    assertEquals(5, queue.size());
    assertFalse(queue.isEmpty());

    queue.clear();

    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());
  }

  @Test
  void emptyQueueThrows() {
    BucketQueue<String> queue = new BucketQueue<>(5);

    assertThrows(IllegalStateException.class, queue::dequeueMax);
    assertThrows(IllegalStateException.class, queue::peekMax);
  }

  @Test
  void rejectsInvalidLevelsAndValues() {
    BucketQueue<String> queue = new BucketQueue<>(5);

    assertThrows(IllegalArgumentException.class, () -> queue.enqueue("x", 0));
    assertThrows(IllegalArgumentException.class, () -> queue.enqueue("x", 6));
    assertThrows(NullPointerException.class, () -> queue.enqueue(null, 3));
    assertThrows(IllegalArgumentException.class, () -> new BucketQueue<String>(0));
  }

  @Test
  void exposesMaxLevel() {
    assertEquals(5, new BucketQueue<String>(5).maxLevel());
  }
}
