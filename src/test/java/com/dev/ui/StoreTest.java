package com.dev.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dev.db.Database;
import com.dev.db.Repositories;
import com.dev.domain.DistributionCenter;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.domain.Vehicle;
import com.dev.modules.Centers;

class StoreTest {

  private Database database;
  private Store store;

  @BeforeEach
  void setUp() {
    database = Database.inMemory();
    store = new Store(Repositories.jdbc(database));
  }

  @AfterEach
  void tearDown() {
    database.close();
  }

  @Test
  void seedMeetsMinimumVolumes() {
    assertTrue(store.packages().size() >= 50, "expected at least 50 packages");
    assertTrue(store.routes().size() >= 12, "expected at least 12 routes");
    assertTrue(store.zones().size() >= 12, "expected at least 12 zones");
    assertTrue(store.centers().size() >= 12, "expected at least 12 centers");
    assertFalse(store.vehicles().isEmpty());
  }

  @Test
  void seedRoutesReferenceKnownZones() {
    for (Route route : store.routes()) {
      assertTrue(store.findZone(route.originId()).isPresent());
      assertTrue(store.findZone(route.destinyId()).isPresent());
    }
  }

  @Test
  void seedPackagesReferenceKnownRoutes() {
    for (Package pkg : store.packages()) {
      boolean knownRoute = store.routes().stream().anyMatch(route -> route.id() == pkg.routeId());
      assertTrue(knownRoute, "package " + pkg.idGuia() + " points to a missing route");
    }
  }

  @Test
  void seedCentersFormASingleHierarchy() {
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
    assertEquals("Campinas", store.cityName(1));
    assertEquals("Zona 999", store.cityName(999));
  }

  @Test
  void mutationsArePersistedAndAudited() {
    Vehicle vehicle = new Vehicle(store.nextVehicleId(), "ZZZ-9Z99", 750f);
    store.saveVehicle(vehicle);

    assertTrue(store.vehicles().contains(vehicle));
    assertTrue(store.auditEvents(10).stream().anyMatch(event -> "Vehicle".equals(event.entity())));

    assertTrue(store.deleteVehicle(vehicle.id()));
    assertFalse(store.vehicles().contains(vehicle));
  }

  @Test
  void packageStatusChangeIsPersisted() {
    Package first = store.packages().get(0);

    store.updatePackageStatus(first.idGuia(), com.dev.domain.DeliveryStatus.DELIVERED);

    Package reloaded = store.packages().stream()
        .filter(pkg -> pkg.idGuia().equals(first.idGuia())).findFirst().orElseThrow();

    assertEquals(com.dev.domain.DeliveryStatus.DELIVERED, reloaded.status());
  }

  @Test
  void reloadKeepsDataConsistent() {
    store.reload();

    assertFalse(store.packages().isEmpty());
    assertFalse(store.zones().isEmpty());
  }
}
