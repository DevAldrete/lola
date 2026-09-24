package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

    tabs.addChangeListener(event -> {
      Component selected = tabs.getSelectedComponent();
      if (selected instanceof Refreshable refreshable) {
        refreshable.refresh();
      }
    });

    add(tabs, BorderLayout.CENTER);

    setSize(1120, 740);
    setMinimumSize(new Dimension(960, 640));
    setLocationRelativeTo(null);
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
