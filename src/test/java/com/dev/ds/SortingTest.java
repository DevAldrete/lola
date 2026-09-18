package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

class SortingTest {

  private static final List<Integer> NUMBERS = List.of(5, 1, 4, 2, 8, 1, 3);
  private static final List<Integer> ASCENDING = List.of(1, 1, 2, 3, 4, 5, 8);

  @Test
  void mergeSortOrdersAscending() {
    assertEquals(ASCENDING, Sorting.mergeSort(NUMBERS, Comparator.naturalOrder()));
  }

  @Test
  void quickSortOrdersAscending() {
    assertEquals(ASCENDING, Sorting.quickSort(NUMBERS, Comparator.naturalOrder()));
  }

  @Test
  void bothHandleEmptyAndSingleElementLists() {
    assertEquals(List.of(), Sorting.mergeSort(List.<Integer>of(), Comparator.naturalOrder()));
    assertEquals(List.of(), Sorting.quickSort(List.<Integer>of(), Comparator.naturalOrder()));
    assertEquals(List.of(7), Sorting.mergeSort(List.of(7), Comparator.naturalOrder()));
    assertEquals(List.of(7), Sorting.quickSort(List.of(7), Comparator.naturalOrder()));
  }

  @Test
  void sortsDescendingWithReversedComparator() {
    List<Integer> descending = List.of(8, 5, 4, 3, 2, 1, 1);

    assertEquals(descending, Sorting.mergeSort(NUMBERS, Comparator.reverseOrder()));
    assertEquals(descending, Sorting.quickSort(NUMBERS, Comparator.reverseOrder()));
  }

  @Test
  void mergeSortIsStable() {
    record Item(String name, int key) {
    }

    List<Item> items = List.of(
        new Item("a", 2),
        new Item("b", 1),
        new Item("c", 2),
        new Item("d", 1));

    List<Item> sorted = Sorting.mergeSort(items, Comparator.comparingInt(Item::key));

    assertEquals(List.of("b", "d", "a", "c"), sorted.stream().map(Item::name).toList());
  }

  @Test
  void doesNotMutateInput() {
    List<Integer> input = new ArrayList<>(NUMBERS);

    Sorting.mergeSort(input, Comparator.naturalOrder());
    Sorting.quickSort(input, Comparator.naturalOrder());

    assertEquals(NUMBERS, input);
  }
}
