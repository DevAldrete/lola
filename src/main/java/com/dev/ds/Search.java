package com.dev.ds;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Binary search over lists already ordered by the given comparator. */
public final class Search {

  private Search() {
  }

  /**
   * Returns the index of {@code target}, or {@code -1} when it is absent.
   */
  public static <T> int binarySearch(List<T> sorted, T target, Comparator<? super T> comparator) {
    Objects.requireNonNull(sorted, "sorted must not be null");
    Objects.requireNonNull(target, "target must not be null");
    Objects.requireNonNull(comparator, "comparator must not be null");

    int low = 0;
    int high = sorted.size() - 1;

    while (low <= high) {
      int middle = low + (high - low) / 2;
      int cmp = comparator.compare(sorted.get(middle), target);

      if (cmp < 0) {
        low = middle + 1;
      } else if (cmp > 0) {
        high = middle - 1;
      } else {
        return middle;
      }
    }

    return -1;
  }

  /**
   * Returns the first index whose element is not less than {@code target};
   * equals {@code sorted.size()} when every element is smaller.
   */
  public static <T> int lowerBound(List<T> sorted, T target, Comparator<? super T> comparator) {
    Objects.requireNonNull(sorted, "sorted must not be null");
    Objects.requireNonNull(target, "target must not be null");
    Objects.requireNonNull(comparator, "comparator must not be null");

    int low = 0;
    int high = sorted.size();

    while (low < high) {
      int middle = low + (high - low) / 2;

      if (comparator.compare(sorted.get(middle), target) < 0) {
        low = middle + 1;
      } else {
        high = middle;
      }
    }

    return low;
  }
}
