package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class HashMapTest {

  @Test
  void putStoresAndGetRetrieves() {
    HashMap<String, Integer> map = new HashMap<>(4);

    map.put("a", 1);
    map.put("b", 2);

    assertEquals(2, map.size());
    assertEquals(1, map.get("a"));
    assertNull(map.get("missing"));
  }

  @Test
  void putOverwritesExistingKey() {
    HashMap<String, Integer> map = new HashMap<>(4);

    map.put("a", 1);
    map.put("a", 9);

    assertEquals(1, map.size());
    assertEquals(9, map.get("a"));
  }

  @Test
  void keysAndValuesReflectEntries() {
    HashMap<String, Integer> map = new HashMap<>(4);

    map.put("a", 1);
    map.put("b", 2);
    map.put("c", 3);

    assertEquals(3, map.keys().size());
    assertTrue(map.keys().containsAll(List.of("a", "b", "c")));
    assertEquals(3, map.values().size());
    assertTrue(map.values().containsAll(List.of(1, 2, 3)));
  }

  @Test
  void removeReturnsValueAndShrinks() {
    HashMap<String, Integer> map = new HashMap<>(4);

    map.put("a", 1);
    map.put("b", 2);

    assertEquals(1, map.remove("a"));
    assertEquals(1, map.size());
    assertFalse(map.containsKey("a"));
    assertNull(map.remove("a"));
  }

  @Test
  void resizingKeepsEveryEntry() {
    HashMap<Integer, Integer> map = new HashMap<>(2);

    for (int i = 0; i < 100; i++) {
      map.put(i, i * 10);
    }

    assertEquals(100, map.size());

    for (int i = 0; i < 100; i++) {
      assertEquals(i * 10, map.get(i));
    }
  }
}
