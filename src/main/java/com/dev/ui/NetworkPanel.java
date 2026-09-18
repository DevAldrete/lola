package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.dev.domain.Route;
import com.dev.domain.Zone;
import com.dev.ds.HashMap;
import com.dev.ds.WeightedGraph;
import com.dev.modules.Routing;

/** Transportation network: weighted shortest paths, route ordering and zones. */
public final class NetworkPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final DefaultTableModel model;
  private final JComboBox<String> metric = new JComboBox<>(new String[] { "Costo", "Distancia", "Tiempo" });
  private final JTextField fromField = new JTextField(4);
  private final JTextField toField = new JTextField(4);
  private final JLabel pathLabel = new JLabel(" ");
  private final JLabel weightLabel = new JLabel(" ");
  private final JLabel cumulativeLabel = new JLabel(" ");
  private final JTextArea partitionsArea = new JTextArea(7, 40);

  public NetworkPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    model = new DefaultTableModel(
        new Object[] { "Id", "Origen", "Destino", "Distancia", "Tiempo", "Costo" }, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(24);
    table.getTableHeader().setReorderingAllowed(false);

    add(buildMetricBar(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildBottom(), BorderLayout.SOUTH);

    refresh();
  }

  private JPanel buildMetricBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    bar.add(new JLabel("Ordenar rutas por:"));
    metric.addActionListener(event -> refresh());
    bar.add(metric);

    return bar;
  }

  private JPanel buildBottom() {
    JPanel bottom = new JPanel(new BorderLayout(8, 8));

    JPanel pathBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    pathBar.add(new JLabel("Ruta más corta desde zona"));
    pathBar.add(fromField);
    pathBar.add(new JLabel("hasta"));
    pathBar.add(toField);

    JButton find = new JButton("Buscar ruta");
    find.addActionListener(event -> findPath());
    pathBar.add(find);

    JPanel results = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
    results.add(pathLabel);
    results.add(weightLabel);
    results.add(cumulativeLabel);
    pathBar.add(results);

    bottom.add(pathBar, BorderLayout.NORTH);

    partitionsArea.setEditable(false);
    partitionsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    JPanel partitionPanel = new JPanel(new BorderLayout());
    partitionPanel.setBorder(BorderFactory.createTitledBorder("Zonas por estado (divide y vencerás)"));
    partitionPanel.add(new JScrollPane(partitionsArea), BorderLayout.CENTER);

    bottom.add(partitionPanel, BorderLayout.CENTER);

    return bottom;
  }

  @Override
  public void refresh() {
    List<Route> routes = switch ((String) metric.getSelectedItem()) {
      case "Distancia" -> Routing.sortByDistance(store.routes());
      case "Tiempo" -> Routing.sortByTime(store.routes());
      default -> Routing.sortByExpense(store.routes());
    };

    model.setRowCount(0);
    for (Route route : routes) {
      model.addRow(new Object[] {
          route.id(),
          store.cityName(route.originId()),
          store.cityName(route.destinyId()),
          Format.distance(route.distanceMeters()),
          Format.duration(route.estimatedTime()),
          Format.money(route.expenseInCents()) });
    }

    renderPartitions();
  }

  private void renderPartitions() {
    HashMap<String, List<Zone>> partitions = Routing.partitionByState(store.zones());
    StringBuilder text = new StringBuilder();

    for (String state : partitions.keys()) {
      text.append(state).append(" (").append(partitions.get(state).size()).append("): ");

      String cities = String.join(", ",
          partitions.get(state).stream().map(Zone::city).toList());

      text.append(cities).append('\n');
    }

    partitionsArea.setText(text.toString());
  }

  private void findPath() {
    try {
      int from = Integer.parseInt(fromField.getText().trim());
      int to = Integer.parseInt(toField.getText().trim());

      WeightedGraph<Integer> graph = Routing.weightedNetwork(store.routes(), metric());

      Optional<WeightedGraph.Path<Integer>> path = Routing.weightedShortestPath(graph, from, to);

      if (path.isEmpty()) {
        pathLabel.setText("Sin ruta entre las zonas " + from + " y " + to);
        weightLabel.setText(" ");
        cumulativeLabel.setText(" ");
        return;
      }

      List<Integer> vertices = path.get().vertices();

      pathLabel.setText(String.join(" -> ", vertices.stream().map(store::cityName).toList()));
      weightLabel.setText("Costo (" + metric.getSelectedItem() + "): "
          + formatWeight(path.get().weight()));

      List<Route> chain = chainOf(vertices);

      cumulativeLabel.setText("Acumulado: " + Format.duration(Routing.cumulativeTime(chain))
          + " | " + Format.money(Routing.cumulativeCost(chain))
          + " | " + Format.distance(Routing.cumulativeDistance(chain)));
    } catch (NumberFormatException exception) {
      pathLabel.setText("Ingrese ids de zona numéricos");
      weightLabel.setText(" ");
      cumulativeLabel.setText(" ");
    } catch (IllegalArgumentException exception) {
      pathLabel.setText(exception.getMessage());
      weightLabel.setText(" ");
      cumulativeLabel.setText(" ");
    }
  }

  private Routing.RouteWeight metric() {
    return switch ((String) metric.getSelectedItem()) {
      case "Distancia" -> Routing.RouteWeight.DISTANCE;
      case "Tiempo" -> Routing.RouteWeight.TIME;
      default -> Routing.RouteWeight.COST;
    };
  }

  private String formatWeight(double value) {
    return switch (metric()) {
      case DISTANCE -> Format.distance(value);
      case TIME -> Format.duration(Duration.ofSeconds((long) value));
      case COST -> Format.money((long) value);
    };
  }

  private List<Route> chainOf(List<Integer> vertices) {
    List<Route> chain = new ArrayList<>();

    for (int i = 0; i < vertices.size() - 1; i++) {
      int origin = vertices.get(i);
      int destiny = vertices.get(i + 1);

      for (Route route : store.routes()) {
        if (route.originId() == origin && route.destinyId() == destiny) {
          chain.add(route);
          break;
        }
      }
    }

    return chain;
  }
}
