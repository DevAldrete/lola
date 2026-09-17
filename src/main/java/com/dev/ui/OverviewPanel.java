package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Route;
import com.dev.modules.Analytics;
import com.dev.modules.Deliveries;

/** Landing tab with the headline numbers for the whole operation. */
public final class OverviewPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final JLabel packages = new JLabel();
  private final JLabel delivered = new JLabel();
  private final JLabel revenue = new JLabel();
  private final JLabel routes = new JLabel();
  private final JLabel distance = new JLabel();
  private final JLabel expense = new JLabel();
  private final JLabel zones = new JLabel();
  private final JLabel vehicles = new JLabel();
  private final JLabel topRoute = new JLabel();
  private final JLabel heaviest = new JLabel();

  public OverviewPanel(Store store) {
    this.store = store;

    setLayout(new GridLayout(0, 2, 12, 12));
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    add(card("Packages", packages));
    add(card("Delivered", delivered));
    add(card("Revenue", revenue));
    add(card("Routes", routes));
    add(card("Network distance", distance));
    add(card("Route expense", expense));
    add(card("Zones", zones));
    add(card("Vehicles", vehicles));
    add(card("Most expensive route", topRoute));
    add(card("Heaviest package", heaviest));

    refresh();
  }

  private static JPanel card(String title, JLabel value) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder(title));

    value.setFont(value.getFont().deriveFont(Font.BOLD, 18f));
    value.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));
    panel.add(value, BorderLayout.CENTER);

    return panel;
  }

  @Override
  public void refresh() {
    packages.setText(String.valueOf(store.packages().size()));
    delivered.setText(String.valueOf(
        Deliveries.filterByStatus(store.packages(), DeliveryStatus.DELIVERED).size()));
    revenue.setText(Format.money(Analytics.revenueInCents(store.packages())));
    routes.setText(String.valueOf(store.routes().size()));
    distance.setText(Format.distance(Analytics.totalDistance(store.routes())));
    expense.setText(Format.money(Analytics.totalExpense(store.routes())));
    zones.setText(String.valueOf(store.zones().size()));
    vehicles.setText(String.valueOf(store.vehicles().size()));
    topRoute.setText(Analytics.mostExpensiveRoute(store.routes())
        .map(OverviewPanel::describeRoute).orElse("-"));
    heaviest.setText(Analytics.heaviestPackage(store.packages())
        .map(OverviewPanel::describePackage).orElse("-"));
  }

  private static String describeRoute(Route route) {
    return "#" + route.id() + "  " + Format.money(route.expenseInCents());
  }

  private static String describePackage(Package pkg) {
    return pkg.waybill() + "  " + pkg.weight() + " kg";
  }
}
