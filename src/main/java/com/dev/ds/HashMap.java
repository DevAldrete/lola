package com.dev.ds;

public class HashMap<K, V> {

  private static class Node<K, V> {
    K key;
    V value;
    Node<K, V> next;

    public Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private Node<K, V>[] bucket;
  private int capacity;
  private int size;

  private static final double LOAD_FACTOR = 0.75;

  @SuppressWarnings("unchecked")
  public HashMap(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0");
    }

    this.capacity = capacity;
    this.size = 0;
    this.bucket = (Node<K, V>[]) new Node[capacity];
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int capacity() {
    return capacity;
  }

  public int size() {
    return size;
  }

  public void put(K key, V value) {
    int index = indexFor(key);

    Node<K, V> current = bucket[index];

    // Key already exists -> update its value
    while (current != null) {
      if (keysEqual(current.key, key)) {
        current.value = value;
        return;
      }

      current = current.next;
    }

    // Resize before inserting if necessary
    if ((double) (size + 1) / capacity > LOAD_FACTOR) {
      resize(capacity * 2);
      index = indexFor(key);
    }

    // Insert at the beginning of the bucket's linked list
    Node<K, V> newNode = new Node<>(key, value);
    newNode.next = bucket[index];
    bucket[index] = newNode;

    size++;
  }

  public V get(K key) {
    int index = indexFor(key);

    Node<K, V> current = bucket[index];

    while (current != null) {
      if (keysEqual(current.key, key)) {
        return current.value;
      }

      current = current.next;
    }

    return null;
  }

  public boolean containsKey(K key) {
    int index = indexFor(key);

    Node<K, V> current = bucket[index];

    while (current != null) {
      if (keysEqual(current.key, key)) {
        return true;
      }

      current = current.next;
    }

    return false;
  }

  public V remove(K key) {
    int index = indexFor(key);

    Node<K, V> current = bucket[index];
    Node<K, V> previous = null;

    while (current != null) {
      if (keysEqual(current.key, key)) {

        if (previous == null) {
          // Removing first node in bucket
          bucket[index] = current.next;
        } else {
          // Removing node in the middle/end
          previous.next = current.next;
        }

        size--;
        return current.value;
      }

      previous = current;
      current = current.next;
    }

    return null;
  }

  private int indexFor(K key) {
    if (key == null) {
      return 0;
    }

    return (key.hashCode() & 0x7fffffff) % capacity;
  }

  private boolean keysEqual(K key1, K key2) {
    if (key1 == key2) {
      return true;
    }

    if (key1 == null || key2 == null) {
      return false;
    }

    return key1.equals(key2);
  }

  @SuppressWarnings("unchecked")
  private void resize(int newCapacity) {
    Node<K, V>[] oldBucket = bucket;

    bucket = (Node<K, V>[]) new Node[newCapacity];
    capacity = newCapacity;

    for (Node<K, V> head : oldBucket) {
      Node<K, V> current = head;

      while (current != null) {
        Node<K, V> next = current.next;

        int index = indexFor(current.key);

        current.next = bucket[index];
        bucket[index] = current;

        current = next;
      }
    }
  }
}
