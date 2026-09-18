package com.dev.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.dev.domain.CenterLevel;
import com.dev.domain.DistributionCenter;
import com.dev.ds.HashMap;
import com.dev.ds.Tree;

/**
 * Operations over the distribution hierarchy: assembling national, regional
 * and local centers into a tree and walking it recursively.
 */
public final class Centers {

  private Centers() {
  }

  /**
   * Builds the center hierarchy. Input order does not matter as long as every
   * non-root center points to a parent that eventually reaches the root.
   */
  public static Tree<DistributionCenter> hierarchy(List<DistributionCenter> centers) {
    Objects.requireNonNull(centers, "centers must not be null");

    DistributionCenter root = null;

    for (DistributionCenter center : centers) {
      if (center.isRoot()) {
        root = center;
        break;
      }
    }

    if (root == null) {
      throw new IllegalArgumentException("No root center (parentId = 0) found");
    }

    Tree<DistributionCenter> tree = new Tree<>();
    tree.setRoot(root);

    HashMap<Integer, Tree.Node<DistributionCenter>> nodes = new HashMap<>(Math.max(centers.size() * 2, 2));
    nodes.put(root.id(), tree.root());

    List<DistributionCenter> pending = new ArrayList<>();

    for (DistributionCenter center : centers) {
      if (center.id() != root.id()) {
        pending.add(center);
      }
    }

    boolean attached = true;

    while (!pending.isEmpty() && attached) {
      attached = false;
      Iterator<DistributionCenter> iterator = pending.iterator();

      while (iterator.hasNext()) {
        DistributionCenter center = iterator.next();
        Tree.Node<DistributionCenter> parent = nodes.get(center.parentId());

        if (parent != null) {
          nodes.put(center.id(), tree.addChild(parent, center));
          iterator.remove();
          attached = true;
        }
      }
    }

    if (!pending.isEmpty()) {
      throw new IllegalArgumentException("Centers reference unknown parents: " + pending);
    }

    return tree;
  }

  /** Returns the centers in pre-order: each parent before its children. */
  public static List<DistributionCenter> nationalToLocal(List<DistributionCenter> centers) {
    return hierarchy(centers).preOrder();
  }

  /** Groups the centers by their hierarchy level. */
  public static HashMap<CenterLevel, List<DistributionCenter>> byLevel(
      List<DistributionCenter> centers) {
    Objects.requireNonNull(centers, "centers must not be null");

    HashMap<CenterLevel, List<DistributionCenter>> grouped = new HashMap<>(8);

    for (DistributionCenter center : centers) {
      List<DistributionCenter> bucket = grouped.get(center.level());

      if (bucket == null) {
        bucket = new ArrayList<>();
        grouped.put(center.level(), bucket);
      }

      bucket.add(center);
    }

    return grouped;
  }
}
