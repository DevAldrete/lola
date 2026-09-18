package com.dev.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TreeTest {

  private static Tree<String> hierarchy() {
    Tree<String> tree = new Tree<>();
    tree.setRoot("Nacional");
    tree.addChild("Nacional", "Regional-SP");
    tree.addChild("Nacional", "Regional-RJ");
    tree.addChild("Regional-SP", "Local-Campinas");
    tree.addChild("Regional-SP", "Local-Santos");

    return tree;
  }

  @Test
  void startsEmptyWithoutRoot() {
    Tree<String> tree = new Tree<>();

    assertTrue(tree.isEmpty());
    assertEquals(0, tree.size());
    assertThrows(IllegalStateException.class, tree::root);
  }

  @Test
  void setRootAndAddChildTrackSize() {
    Tree<String> tree = hierarchy();

    assertEquals(5, tree.size());
    assertFalse(tree.isEmpty());
    assertEquals("Nacional", tree.root().value());
  }

  @Test
  void preOrderVisitsParentBeforeChildren() {
    assertEquals(
        List.of("Nacional", "Regional-SP", "Local-Campinas", "Local-Santos", "Regional-RJ"),
        hierarchy().preOrder());
  }

  @Test
  void levelsGroupByDepth() {
    assertEquals(
        List.of(
            List.of("Nacional"),
            List.of("Regional-SP", "Regional-RJ"),
            List.of("Local-Campinas", "Local-Santos")),
        hierarchy().levels());
  }

  @Test
  void findLocatesNodes() {
    Tree<String> tree = hierarchy();

    assertEquals("Local-Santos", tree.find("Local-Santos").orElseThrow().value());
    assertTrue(tree.find("Missing").isEmpty());
  }

  @Test
  void addChildRejectsUnknownParent() {
    assertThrows(IllegalArgumentException.class, () -> hierarchy().addChild("Missing", "Leaf"));
  }
}
