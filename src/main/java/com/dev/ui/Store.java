package com.dev.ui;

import java.util.List;
import java.util.Optional;

import com.dev.data.Seed;
import com.dev.domain.DistributionCenter;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.domain.Vehicle;
import com.dev.domain.Zone;

/**
 * In-memory application data. Lists are immutable; updates replace them so the
 * stateless module functions always operate on a consistent snapshot.
 */
public final class Store {

  private List<Package> packages;
  private final List<Route> routes;
  private final List<Zone> zones;
  private final List<DistributionCenter> centers;
  private List<Vehicle> vehicles;

  public Store() {
    this.packages = Seed.packages();
    this.routes = Seed.routes();
    this.zones = Seed.zones();
    this.centers = Seed.centers();
    this.vehicles = Seed.vehicles();
  }

  public List<Package> packages() {
    return packages;
  }

  public void setPackages(List<Package> packages) {
    this.packages = List.copyOf(packages);
  }

  public List<Route> routes() {
    return routes;
  }

  public List<Zone> zones() {
    return zones;
  }

  public List<DistributionCenter> centers() {
    return centers;
  }

  public List<Vehicle> vehicles() {
    return vehicles;
  }

  public void setVehicles(List<Vehicle> vehicles) {
    this.vehicles = List.copyOf(vehicles);
  }

  public Optional<Zone> findZone(int zoneId) {
    for (Zone zone : zones) {
      if (zone.id() == zoneId) {
        return Optional.of(zone);
      }
    }

    return Optional.empty();
  }

  public String cityName(int zoneId) {
    return findZone(zoneId).map(Zone::city).orElse("Zona " + zoneId);
  }
}
