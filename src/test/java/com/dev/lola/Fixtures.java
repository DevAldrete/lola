package com.dev.lola;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Priority;

/** Shared factories for building test data without repeating constructor noise. */
public final class Fixtures {

  public static final int ROUTE_ID = 1;
  public static final float WEIGHT = 1f;
  public static final long PRICE_IN_CENTS = 1000L;

  private Fixtures() {
  }

  public static Package pkg(int id, String waybill, Priority priority) {
    return pkg(id, waybill, priority, DeliveryStatus.CREATED);
  }

  public static Package pkg(int id, String waybill, Priority priority, DeliveryStatus status) {
    return pkg(id, waybill, ROUTE_ID, WEIGHT, PRICE_IN_CENTS, priority, status);
  }

  public static Package pkg(int id, String waybill, int routeId, float weight, long priceInCents,
      Priority priority, DeliveryStatus status) {
    return new Package(id, waybill, routeId, weight, priceInCents, priority, status);
  }
}
