package com.dev.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dev.domain.DistributionCenter;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.modules.Centers;

class StoreTest {

  @Test
  void seedMeetsMinimumVolumes() {
    Store store = new Store();

    assertTrue(store.packages().size() >= 50, "expected at least 50 packages");
    assertTrue(store.routes().size() >= 12, "expected at least 12 routes");
    assertTrue(store.zones().size() >= 12, "expected at least 12 zones");
    assertTrue(store.centers().size() >= 12, "expected at least 12 centers");
    assertFalse(store.vehicles().isEmpty());
  }

  @Test
  void seedRoutesReferenceKnownZones() {
    Store store = new Store();

    for (Route route : store.routes()) {
      assertTrue(store.findZone(route.originId()).isPresent());
      assertTrue(store.findZone(route.destinyId()).isPresent());
    }
  }

  @Test
  void seedPackagesReferenceKnownRoutes() {
    Store store = new Store();

    for (Package pkg : store.packages()) {
      boolean knownRoute = store.routes().stream().anyMatch(route -> route.id() == pkg.routeId());
      assertTrue(knownRoute, "package " + pkg.idGuia() + " points to a missing route");
    }
  }

  @Test
  void seedCentersFormASingleHierarchy() {
    Store store = new Store();

    long roots = store.centers().stream().filter(DistributionCenter::isRoot).count();
    assertEquals(1, roots);

    for (DistributionCenter center : store.centers()) {
      boolean parentExists = center.isRoot()
          || store.centers().stream().anyMatch(other -> other.id() == center.parentId());
      assertTrue(parentExists, "center " + center.name() + " has an unknown parent");
    }

    assertEquals(store.centers().size(), Centers.hierarchy(store.centers()).size());
  }

  @Test
  void cityNameFallsBackForUnknownZone() {
    Store store = new Store();

    assertEquals("Campinas", store.cityName(1));
    assertEquals("Zona 999", store.cityName(999));
  }

  @Test
  void setPackagesReplacesData() {
    Store store = new Store();

    store.setPackages(List.of());

    assertTrue(store.packages().isEmpty());
  }
}
