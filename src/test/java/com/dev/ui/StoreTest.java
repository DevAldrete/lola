package com.dev.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dev.domain.Package;
import com.dev.domain.Route;

class StoreTest {

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
      assertTrue(knownRoute, "package " + pkg.waybill() + " points to a missing route");
    }
  }

  @Test
  void seedHasVehicles() {
    assertFalse(new Store().vehicles().isEmpty());
  }

  @Test
  void cityNameFallsBackForUnknownZone() {
    Store store = new Store();

    assertEquals("Campinas", store.cityName(1));
    assertEquals("Zone 99", store.cityName(99));
  }

  @Test
  void setPackagesReplacesData() {
    Store store = new Store();

    store.setPackages(List.of());

    assertTrue(store.packages().isEmpty());
  }
}
