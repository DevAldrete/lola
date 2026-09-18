package com.dev.ui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Priority;
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
  private List<Vehicle> vehicles;

  public Store() {
    this.packages = samplePackages();
    this.routes = sampleRoutes();
    this.zones = sampleZones();
    this.vehicles = sampleVehicles();
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

  public List<Vehicle> vehicles() {
    return vehicles;
  }

  public void setVehicles(List<Vehicle> vehicles) {
    this.vehicles = List.copyOf(vehicles);
  }

  public Optional<Zone> findZone(int zoneId) {
    return zones.stream().filter(zone -> zone.id() == zoneId).findFirst();
  }

  public String cityName(int zoneId) {
    return findZone(zoneId).map(Zone::city).orElse("Zone " + zoneId);
  }

  private static List<Zone> sampleZones() {
    return List.of(
        new Zone(1, "SP", "Campinas"),
        new Zone(2, "SP", "Santos"),
        new Zone(3, "RJ", "Rio de Janeiro"),
        new Zone(4, "MG", "Belo Horizonte"),
        new Zone(5, "PR", "Curitiba"),
        new Zone(6, "BA", "Salvador"));
  }

  private static List<Route> sampleRoutes() {
    return List.of(
        new Route(1, 1, 2, 120_000, Duration.ofHours(2), 85_000),
        new Route(2, 2, 3, 430_000, Duration.ofHours(7), 240_000),
        new Route(3, 1, 4, 490_000, Duration.ofHours(8), 260_000),
        new Route(4, 4, 6, 1_370_000, Duration.ofHours(20), 720_000),
        new Route(5, 3, 5, 850_000, Duration.ofHours(12), 430_000),
        new Route(6, 5, 1, 400_000, Duration.ofHours(6), 210_000));
  }

  private static List<Package> samplePackages() {
    LocalDateTime base = LocalDateTime.of(2026, 1, 10, 18, 0);

    return List.of(
        new Package(1, "LOLA-0001", 1, 2.5f, 3_500, base.plusDays(2), Priority.IMPORTANT, DeliveryStatus.CREATED),
        new Package(2, "LOLA-0002", 2, 8.0f, 12_900, base, Priority.CRITICAL, DeliveryStatus.IN_TRANSIT),
        new Package(3, "LOLA-0003", 3, 1.2f, 2_400, base.plusDays(5), Priority.MODERATE, DeliveryStatus.CREATED),
        new Package(4, "LOLA-0004", 4, 15.5f, 45_900, base.plusDays(9), Priority.NORMAL, DeliveryStatus.DISPATCHED),
        new Package(5, "LOLA-0005", 5, 3.3f, 7_800, base.plusDays(1), Priority.IMPORTANT, DeliveryStatus.DELIVERED),
        new Package(6, "LOLA-0006", 6, 0.8f, 1_500, base, Priority.CRITICAL, DeliveryStatus.CREATED),
        new Package(7, "LOLA-0007", 1, 5.0f, 6_400, base.plusDays(4), Priority.MODERATE, DeliveryStatus.DELIVERED),
        new Package(8, "LOLA-0008", 2, 12.0f, 21_000, base.plusDays(3), Priority.URGENT, DeliveryStatus.IN_TRANSIT));
  }

  private static List<Vehicle> sampleVehicles() {
    return List.of(
        new Vehicle(1, "ABC-1A23", 1_200f),
        new Vehicle(2, "DEF-4B56", 3_500f),
        new Vehicle(3, "GHI-7C89", 600f),
        new Vehicle(4, "JKL-0D12", 8_000f),
        new Vehicle(5, "MNO-3E45", 2_200f));
  }
}
