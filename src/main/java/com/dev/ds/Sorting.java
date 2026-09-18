package com.dev.ds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Sorting algorithms implemented from scratch. Both return a new list and
 * never mutate the input. Merge sort is stable; quick sort is in-place on a
 * private copy.
 */
public final class Sorting {

  private Sorting() {
  }

  /** Stable O(n log n) sort. */
  public static <T> List<T> mergeSort(List<T> values, Comparator<? super T> comparator) {
    Objects.requireNonNull(values, "values must not be null");
    Objects.requireNonNull(comparator, "comparator must not be null");

    return mergeSortRecursive(new ArrayList<>(values), comparator);
  }

  private static <T> List<T> mergeSortRecursive(List<T> values, Comparator<? super T> comparator) {
    if (values.size() <= 1) {
      return values;
    }

    int middle = values.size() / 2;

    List<T> left = mergeSortRecursive(new ArrayList<>(values.subList(0, middle)), comparator);
    List<T> right = mergeSortRecursive(new ArrayList<>(values.subList(middle, values.size())),
        comparator);

    return merge(left, right, comparator);
  }

  private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> comparator) {
    List<T> result = new ArrayList<>(left.size() + right.size());

    int i = 0;
    int j = 0;

    while (i < left.size() && j < right.size()) {
      if (comparator.compare(left.get(i), right.get(j)) <= 0) {
        result.add(left.get(i++));
      } else {
        result.add(right.get(j++));
      }
    }

    while (i < left.size()) {
      result.add(left.get(i++));
    }

    while (j < right.size()) {
      result.add(right.get(j++));
    }

    return result;
  }

  /** O(n log n) average-case in-place sort of a private copy. */
  public static <T> List<T> quickSort(List<T> values, Comparator<? super T> comparator) {
    Objects.requireNonNull(values, "values must not be null");
    Objects.requireNonNull(comparator, "comparator must not be null");

    List<T> result = new ArrayList<>(values);
    quickSort(result, 0, result.size() - 1, comparator);

    return result;
  }

  private static <T> void quickSort(List<T> values, int low, int high,
      Comparator<? super T> comparator) {
    if (low >= high) {
      return;
    }

    int pivot = partition(values, low, high, comparator);

    quickSort(values, low, pivot - 1, comparator);
    quickSort(values, pivot + 1, high, comparator);
  }

  private static <T> int partition(List<T> values, int low, int high,
      Comparator<? super T> comparator) {
    T pivot = values.get(high);
    int boundary = low - 1;

    for (int i = low; i < high; i++) {
      if (comparator.compare(values.get(i), pivot) <= 0) {
        boundary++;
        swap(values, boundary, i);
      }
    }

    swap(values, boundary + 1, high);

    return boundary + 1;
  }

  private static <T> void swap(List<T> values, int a, int b) {
    T temporary = values.get(a);
    values.set(a, values.get(b));
    values.set(b, temporary);
  }
}
