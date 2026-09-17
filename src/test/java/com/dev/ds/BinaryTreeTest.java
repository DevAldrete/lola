package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dev.ds.BinaryTree.Order;

class BinaryTreeTest {

  private static BinaryTree<Integer> sample() {
    BinaryTree<Integer> tree = new BinaryTree<>();

    for (int value : new int[] { 50, 30, 70, 20, 40, 60, 80 }) {
      tree.push(value);
    }

    return tree;
  }

  @Test
  void startsEmpty() {
    BinaryTree<Integer> tree = new BinaryTree<>();

    assertTrue(tree.isEmpty());
    assertEquals(0, tree.size());
  }

  @Test
  void pushIncreasesSizeAndIgnoresDuplicates() {
    BinaryTree<Integer> tree = sample();

    assertEquals(7, tree.size());
    assertFalse(tree.isEmpty());

    tree.push(50);

    assertEquals(7, tree.size());
  }

  @Test
  void pushRejectsNull() {
    assertThrows(NullPointerException.class, () -> new BinaryTree<Integer>().push(null));
  }

  @Test
  void containsReflectsInsertedValues() {
    BinaryTree<Integer> tree = sample();

    assertTrue(tree.contains(40));
    assertTrue(tree.contains(80));
    assertFalse(tree.contains(45));
  }

  @Test
  void inOrderReturnsSortedValues() {
    assertEquals(List.of(20, 30, 40, 50, 60, 70, 80),
        sample().asList(Order.INORDER));
  }

  @Test
  void preOrderReturnsRootFirst() {
    assertEquals(List.of(50, 30, 20, 40, 70, 60, 80),
        sample().asList(Order.PREORDER));
  }

  @Test
  void postOrderReturnsRootLast() {
    assertEquals(List.of(20, 40, 30, 60, 80, 70, 50),
        sample().asList(Order.POSTORDER));
  }

  @Test
  void asListOnEmptyTreeReturnsEmpty() {
    assertTrue(new BinaryTree<Integer>().asList(Order.INORDER).isEmpty());
  }

  @Test
  void deleteRemovesLeaf() {
    BinaryTree<Integer> tree = sample();

    assertTrue(tree.delete(20));

    assertEquals(6, tree.size());
    assertFalse(tree.contains(20));
    assertEquals(List.of(30, 40, 50, 60, 70, 80), tree.asList(Order.INORDER));
  }

  @Test
  void deleteRemovesNodeWithSingleChild() {
    BinaryTree<Integer> tree = sample();

    tree.delete(20);

    assertTrue(tree.delete(30));

    assertEquals(List.of(40, 50, 60, 70, 80), tree.asList(Order.INORDER));
  }

  @Test
  void deleteRemovesNodeWithTwoChildren() {
    BinaryTree<Integer> tree = sample();

    assertTrue(tree.delete(70));

    assertEquals(6, tree.size());
    assertFalse(tree.contains(70));
    assertTrue(tree.contains(80));
    assertEquals(List.of(20, 30, 40, 50, 60, 80), tree.asList(Order.INORDER));
  }

  @Test
  void deleteMissingReturnsFalse() {
    BinaryTree<Integer> tree = sample();

    assertFalse(tree.delete(45));
    assertEquals(7, tree.size());
  }

  @Test
  void popRemovesRootAndKeepsTreeOrdered() {
    BinaryTree<Integer> tree = sample();

    assertEquals(50, tree.pop());

    assertEquals(6, tree.size());
    assertFalse(tree.contains(50));
    assertEquals(List.of(20, 30, 40, 60, 70, 80), tree.asList(Order.INORDER));
  }

  @Test
  void popOnEmptyReturnsNull() {
    assertNull(new BinaryTree<Integer>().pop());
  }

  @Test
  void deletingEveryValueEmptiesTree() {
    BinaryTree<Integer> tree = sample();

    for (int value : new int[] { 50, 30, 70, 20, 40, 60, 80 }) {
      assertTrue(tree.delete(value));
    }

    assertTrue(tree.isEmpty());
    assertEquals(0, tree.size());
    assertTrue(tree.asList(Order.INORDER).isEmpty());
  }
}
