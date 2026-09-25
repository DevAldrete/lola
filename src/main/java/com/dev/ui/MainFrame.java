package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Main window: one tab per concern, refreshed from the store when selected. */
public final class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public MainFrame(Store store) {
    super("PkgLog - Sistema de Gestión Logística");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(buildMenuBar());

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Resumen", new OverviewPanel(store));
    tabs.addTab("Despacho", new DispatchPanel(store));
    tabs.addTab("Rastreo", new TrackingPanel(store));
    tabs.addTab("Red", new NetworkPanel(store));
    tabs.addTab("Repartos", new PartitionPanel(store));
    tabs.addTab("Centros", new CentersPanel(store));
    tabs.addTab("Reportes", new ReportsPanel(store));
    tabs.addTab("Datos", new AdminPanel(store));
    tabs.addTab("Auditoría", new AuditPanel(store));

    tabs.addChangeListener(event -> {
      Component selected = tabs.getSelectedComponent();
      if (selected instanceof Refreshable refreshable) {
        refreshable.refresh();
      }
    });

    add(tabs, BorderLayout.CENTER);
    add(buildStatusBar(store), BorderLayout.SOUTH);

    setSize(1120, 740);
    setMinimumSize(new Dimension(960, 640));
    setLocationRelativeTo(null);
  }

  private static JPanel buildStatusBar(Store store) {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));

    String name = store.currentUser() == null ? "invitado" : store.currentUser().name();
    String role = store.currentUser() == null ? "-" : store.currentUser().role().name();

    bar.add(new JLabel("Sesión: " + name + " (" + role + ")"));
    bar.add(new JLabel(" | "));
    bar.add(new JLabel("Datos: SQLite"));

    return bar;
  }

  private static JMenuBar buildMenuBar() {
    JMenuItem exit = new JMenuItem("Salir");
    exit.addActionListener(event -> System.exit(0));

    JMenu file = new JMenu("Archivo");
    file.add(exit);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(file);

    return menuBar;
  }
}
