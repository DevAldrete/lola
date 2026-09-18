package com.dev.modules;

import static com.dev.lola.Fixtures.pkg;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Priority;
import com.dev.ds.HashMap;
import com.dev.ds.PriorityQueue;

class DeliveriesTest {

  private static List<Package> sample() {
    return List.of(
        pkg(3, "WB-URGENT-3", Priority.URGENT),
        pkg(1, "WB-IMPORTANT-1", Priority.IMPORTANT),
        pkg(2, "WB-URGENT-2", Priority.URGENT),
        pkg(0, "WB-NORMAL-0", Priority.NORMAL));
  }

  @Test
  void urgencyOrdersByPriorityThenId() {
    List<Package> sorted = new ArrayList<>(sample());
    sorted.sort(Deliveries.URGENCY);

    assertEquals(
        List.of("WB-URGENT-2", "WB-URGENT-3", "WB-IMPORTANT-1", "WB-NORMAL-0"),
        sorted.stream().map(Package::waybill).toList());
  }

  @Test
  void toPriorityQueueDequeuesInUrgencyOrder() {
    PriorityQueue<Package> queue = Deliveries.toPriorityQueue(sample());

    assertEquals(4, queue.size());
    assertEquals("WB-URGENT-2", queue.dequeue().waybill());
    assertEquals("WB-URGENT-3", queue.dequeue().waybill());
    assertEquals("WB-IMPORTANT-1", queue.dequeue().waybill());
    assertEquals("WB-NORMAL-0", queue.dequeue().waybill());
  }

  @Test
  void urgentReturnsMostUrgentFirst() {
    List<Package> urgent = Deliveries.urgent(sample(), 2);

    assertEquals(
        List.of("WB-URGENT-2", "WB-URGENT-3"),
        urgent.stream().map(Package::waybill).toList());
  }

  @Test
  void urgentClampsToAvailablePackages() {
    assertEquals(4, Deliveries.urgent(sample(), 10).size());
    assertTrue(Deliveries.urgent(sample(), 0).isEmpty());
    assertTrue(Deliveries.urgent(List.of(), 3).isEmpty());
  }

  @Test
  void urgentRejectsNegativeLimit() {
    assertThrows(IllegalArgumentException.class, () -> Deliveries.urgent(sample(), -1));
  }

  @Test
  void indexByWaybillStoresEveryPackage() {
    HashMap<String, Package> index = Deliveries.indexByWaybill(sample());

    assertEquals(4, index.size());
    assertTrue(index.containsKey("WB-IMPORTANT-1"));
  }

  @Test
  void findByWaybillReturnsTrackedPackage() {
    HashMap<String, Package> index = Deliveries.indexByWaybill(sample());

    Package found = Deliveries.findByWaybill(index, "WB-IMPORTANT-1").orElseThrow();

    assertEquals(1, found.id());
    assertEquals(Priority.IMPORTANT, found.priority());
  }

  @Test
  void findByWaybillReturnsEmptyWhenUnknown() {
    HashMap<String, Package> index = Deliveries.indexByWaybill(sample());

    assertTrue(Deliveries.findByWaybill(index, "WB-MISSING").isEmpty());
  }

  @Test
  void updateStatusReplacesOnlyMatchingWaybill() {
    List<Package> updated = Deliveries.updateStatus(sample(), "WB-IMPORTANT-1", DeliveryStatus.DELIVERED);

    assertNotSame(sample(), updated);
    assertEquals(DeliveryStatus.DELIVERED,
        Deliveries.findByWaybill(Deliveries.indexByWaybill(updated), "WB-IMPORTANT-1").orElseThrow()
            .status());
    assertEquals(1, Deliveries.filterByStatus(updated, DeliveryStatus.DELIVERED).size());
    assertEquals(3, Deliveries.filterByStatus(updated, DeliveryStatus.CREATED).size());
  }

  @Test
  void updateStatusDoesNotMutateInput() {
    List<Package> packages = sample();

    Deliveries.updateStatus(packages, "WB-IMPORTANT-1", DeliveryStatus.DELIVERED);

    assertEquals(DeliveryStatus.CREATED, packages.get(1).status());
  }

  @Test
  void totalsWeightAndPrice() {
    List<Package> packages = List.of(
        pkg(1, "WB-1", 1, 2.5f, 500, Priority.NORMAL, DeliveryStatus.CREATED),
        pkg(2, "WB-2", 1, 1.5f, 1500, Priority.IMPORTANT, DeliveryStatus.CREATED));

    assertEquals(4.0f, Deliveries.totalWeight(packages), 0.0001f);
    assertEquals(2000L, Deliveries.totalPriceInCents(packages));
  }

  @Test
  void filtersPackagesByPriority() {
    assertEquals(2, Deliveries.filterByPriority(sample(), Priority.URGENT).size());
    assertEquals(1, Deliveries.filterByPriority(sample(), Priority.IMPORTANT).size());
    assertTrue(Deliveries.filterByPriority(sample(), Priority.MODERATE).isEmpty());
  }

  @Test
  void countsPackagesByPriority() {
    assertEquals(2, Deliveries.countByPriority(sample(), Priority.URGENT));
    assertEquals(1, Deliveries.countByPriority(sample(), Priority.NORMAL));
    assertEquals(0, Deliveries.countByPriority(sample(), Priority.MODERATE));
  }
}
