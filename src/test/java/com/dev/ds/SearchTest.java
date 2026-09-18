package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

class SearchTest {

  private static final List<Integer> SORTED = List.of(1, 3, 5, 7, 9);

  @Test
  void binarySearchFindsElements() {
    assertEquals(0, Search.binarySearch(SORTED, 1, Comparator.naturalOrder()));
    assertEquals(2, Search.binarySearch(SORTED, 5, Comparator.naturalOrder()));
    assertEquals(4, Search.binarySearch(SORTED, 9, Comparator.naturalOrder()));
  }

  @Test
  void binarySearchReturnsMinusOneWhenAbsent() {
    assertEquals(-1, Search.binarySearch(SORTED, 4, Comparator.naturalOrder()));
    assertEquals(-1, Search.binarySearch(List.of(), 4, Comparator.naturalOrder()));
  }

  @Test
  void lowerBoundFindsInsertionPoint() {
    assertEquals(0, Search.lowerBound(SORTED, 0, Comparator.naturalOrder()));
    assertEquals(2, Search.lowerBound(SORTED, 4, Comparator.naturalOrder()));
    assertEquals(2, Search.lowerBound(SORTED, 5, Comparator.naturalOrder()));
    assertEquals(5, Search.lowerBound(SORTED, 10, Comparator.naturalOrder()));
  }
}
