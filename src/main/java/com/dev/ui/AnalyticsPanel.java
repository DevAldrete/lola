package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.OptionalDouble;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.ds.HashMap;
import com.dev.modules.Analytics;
import com.dev.modules.Deliveries;

/** Read-only breakdown of revenue, costs and delivery progress. */
public final class AnalyticsPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;
  private final JTextArea area = new JTextArea();

  public AnalyticsPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    area.setEditable(false);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    add(new JScrollPane(area), BorderLayout.CENTER);

    refresh();
  }

  @Override
  public void refresh() {
    StringBuilder text = new StringBuilder();

    appendPackages(text);
    text.append('\n');
    appendRoutes(text);

    area.setText(text.toString());
    area.setCaretPosition(0);
  }

  private void appendPackages(StringBuilder text) {
    text.append("PACKAGES").append('\n');
    line(text, "Total", String.valueOf(store.packages().size()));
    line(text, "Revenue", Format.money(Analytics.revenueInCents(store.packages())));
    line(text, "Average price", money(Analytics.averagePriceInCents(store.packages())));
    line(text, "Total weight", Deliveries.totalWeight(store.packages()) + " kg");
    line(text, "Heaviest", Analytics.heaviestPackage(store.packages())
        .map(AnalyticsPanel::describePackage).orElse("-"));

    text.append('\n').append("  By status").append('\n');

    HashMap<DeliveryStatus, Integer> byStatus = Analytics.countByStatus(store.packages());
    for (DeliveryStatus status : DeliveryStatus.values()) {
      Integer count = byStatus.get(status);
      line(text, "    " + status, count == null ? "0" : String.valueOf(count));
    }

    text.append('\n').append("  Revenue by route").append('\n');

    HashMap<Integer, Long> byRoute = Analytics.revenueByRoute(store.packages());
    for (Integer routeId : byRoute.keys()) {
      line(text, "    " + describeRoute(routeId), Format.money(byRoute.get(routeId)));
    }
  }

  private void appendRoutes(StringBuilder text) {
    text.append("ROUTES").append('\n');
    line(text, "Count", String.valueOf(store.routes().size()));
    line(text, "Total distance", Format.distance(Analytics.totalDistance(store.routes())));
    line(text, "Total expense", Format.money(Analytics.totalExpense(store.routes())));
    line(text, "Average expense", money(Analytics.averageExpense(store.routes())));
    line(text, "Most expensive", Analytics.mostExpensiveRoute(store.routes())
        .map(route -> "#" + route.id() + " " + Format.money(route.expenseInCents())).orElse("-"));
    line(text, "Cheapest", Analytics.cheapestRoute(store.routes())
        .map(route -> "#" + route.id() + " " + Format.money(route.expenseInCents())).orElse("-"));
  }

  private static void line(StringBuilder text, String label, String value) {
    text.append(String.format("  %-32s %s%n", label, value));
  }

  private static String money(OptionalDouble value) {
    return value.isPresent() ? Format.money((long) value.getAsDouble()) : "-";
  }

  private String describeRoute(int routeId) {
    for (Route route : store.routes()) {
      if (route.id() == routeId) {
        return "Route " + routeId + " (" + store.cityName(route.originId())
            + " -> " + store.cityName(route.destinyId()) + ")";
      }
    }

    return "Route " + routeId;
  }

  private static String describePackage(Package pkg) {
    return pkg.waybill() + " (" + pkg.weight() + " kg)";
  }
}
