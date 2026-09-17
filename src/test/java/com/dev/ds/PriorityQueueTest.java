package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class PriorityQueueTest {

  private static PriorityQueue<Integer> naturalQueue() {
    return new PriorityQueue<>(Comparator.naturalOrder());
  }

  @Test
  void dequeuesSmallestElementFirst() {
    PriorityQueue<Integer> queue = naturalQueue();

    queue.enqueue(5);
    queue.enqueue(1);
    queue.enqueue(3);

    assertEquals(1, queue.dequeue());
    assertEquals(3, queue.dequeue());
    assertEquals(5, queue.dequeue());
  }

  @Test
  void respectsCustomComparator() {
    PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.reverseOrder());

    queue.enqueue(1);
    queue.enqueue(3);
    queue.enqueue(2);

    assertEquals(3, queue.dequeue());
    assertEquals(2, queue.dequeue());
    assertEquals(1, queue.dequeue());
  }

  @Test
  void peekReturnsHighestPriorityWithoutRemoving() {
    PriorityQueue<Integer> queue = naturalQueue();

    queue.enqueue(9);
    queue.enqueue(4);

    assertEquals(4, queue.peek());
    assertEquals(2, queue.size());
    assertEquals(4, queue.dequeue());
  }

  @Test
  void tracksSizeAndEmptiness() {
    PriorityQueue<Integer> queue = naturalQueue();

    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());

    queue.enqueue(1);

    assertEquals(1, queue.size());
    assertFalse(queue.isEmpty());

    queue.dequeue();

    assertTrue(queue.isEmpty());
  }

  @Test
  void dequeueOnEmptyThrows() {
    assertThrows(IllegalStateException.class, () -> naturalQueue().dequeue());
  }

  @Test
  void peekOnEmptyThrows() {
    assertThrows(IllegalStateException.class, () -> naturalQueue().peek());
  }

  @Test
  void enqueueRejectsNull() {
    assertThrows(NullPointerException.class, () -> naturalQueue().enqueue(null));
  }

  @Test
  void rejectsNonPositiveCapacity() {
    assertThrows(IllegalArgumentException.class,
        () -> new PriorityQueue<>(0, Comparator.naturalOrder()));
  }

  @Test
  void clearRemovesAllElements() {
    PriorityQueue<Integer> queue = naturalQueue();

    queue.enqueue(1);
    queue.enqueue(2);
    queue.clear();

    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());
    assertThrows(IllegalStateException.class, queue::dequeue);
  }

  @Test
  void growsBeyondInitialCapacity() {
    PriorityQueue<Integer> queue = new PriorityQueue<>(2, Comparator.naturalOrder());

    for (int i = 100; i > 0; i--) {
      queue.enqueue(i);
    }

    assertEquals(100, queue.size());

    for (int expected = 1; expected <= 100; expected++) {
      assertEquals(expected, queue.dequeue());
    }

    assertTrue(queue.isEmpty());
  }

  @Test
  void keepsHeapOrderAfterInterleavedOperations() {
    PriorityQueue<Integer> queue = new PriorityQueue<>(3, Comparator.naturalOrder());

    queue.enqueue(7);
    queue.enqueue(2);
    queue.enqueue(9);
    assertEquals(2, queue.dequeue());

    queue.enqueue(1);
    queue.enqueue(5);

    assertEquals(1, queue.dequeue());
    assertEquals(5, queue.dequeue());
    assertEquals(7, queue.dequeue());
    assertEquals(9, queue.dequeue());
  }
}
