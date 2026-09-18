package com.dev.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SeedTest {

  @Test
  void packagesAreDeterministic() {
    assertEquals(Seed.packages(), Seed.packages());
  }

  @Test
  void packagesHaveUniqueWaybills() {
    var packages = Seed.packages();
    long distinct = packages.stream().map(pkg -> pkg.idGuia()).distinct().count();

    assertEquals(packages.size(), distinct);
  }

  @Test
  void routesHaveNonNegativeWeights() {
    for (var route : Seed.routes()) {
      assertTrue(route.distanceMeters() >= 0);
      assertTrue(route.expenseInCents() >= 0);
      assertTrue(!route.estimatedTime().isNegative());
    }
  }
}
