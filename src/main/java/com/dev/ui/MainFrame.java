package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/** Main window: one tab per concern, refreshed from the store when selected. */
public final class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public MainFrame(Store store) {
    super("LOLA - Logistics Oriented Language Assistant");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Overview", new OverviewPanel(store));
    tabs.addTab("Deliveries", new DeliveriesPanel(store));
    tabs.addTab("Routing", new RoutingPanel(store));
    tabs.addTab("Fleet", new FleetPanel(store));
    tabs.addTab("Analytics", new AnalyticsPanel(store));

    tabs.addChangeListener(event -> {
      Component selected = tabs.getSelectedComponent();
      if (selected instanceof Refreshable refreshable) {
        refreshable.refresh();
      }
    });

    add(tabs, BorderLayout.CENTER);

    setSize(1024, 680);
    setLocationRelativeTo(null);
  }
}
