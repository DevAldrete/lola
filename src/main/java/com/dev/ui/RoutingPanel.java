package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

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
import com.dev.ds.Graph;
import com.dev.ds.HashMap;
import com.dev.modules.Routing;

/** Route table with sorting, graph shortest path and zone partitioning. */
public final class RoutingPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final DefaultTableModel model;
  private final JComboBox<String> sortBy = new JComboBox<>(new String[] { "Expense", "Distance", "Time" });
  private final JTextField fromField = new JTextField(4);
  private final JTextField toField = new JTextField(4);
  private final JLabel pathLabel = new JLabel(" ");
  private final JTextArea partitionsArea = new JTextArea(8, 40);

  public RoutingPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    model = new DefaultTableModel(
        new Object[] { "Id", "Origin", "Destiny", "Distance", "Time", "Expense" }, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(24);
    table.getTableHeader().setReorderingAllowed(false);

    add(buildSortBar(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildBottom(), BorderLayout.SOUTH);

    refresh();
  }

  private JPanel buildSortBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    bar.add(new JLabel("Sort routes by:"));
    sortBy.addActionListener(event -> refresh());
    bar.add(sortBy);

    return bar;
  }

  private JPanel buildBottom() {
    JPanel bottom = new JPanel(new BorderLayout(8, 8));

    JPanel pathBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    pathBar.add(new JLabel("Shortest path from zone"));
    pathBar.add(fromField);
    pathBar.add(new JLabel("to"));
    pathBar.add(toField);

    JButton find = new JButton("Find path");
    find.addActionListener(event -> findPath());
    pathBar.add(find);
    pathBar.add(pathLabel);

    bottom.add(pathBar, BorderLayout.NORTH);

    partitionsArea.setEditable(false);
    partitionsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    JPanel partitionPanel = new JPanel(new BorderLayout());
    partitionPanel.setBorder(BorderFactory.createTitledBorder("Zones by state (divide and conquer)"));
    partitionPanel.add(new JScrollPane(partitionsArea), BorderLayout.CENTER);

    bottom.add(partitionPanel, BorderLayout.CENTER);

    return bottom;
  }

  @Override
  public void refresh() {
    List<Route> routes = switch ((String) sortBy.getSelectedItem()) {
      case "Distance" -> Routing.sortByDistance(store.routes());
      case "Time" -> Routing.sortByTime(store.routes());
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

      Graph<Integer> network = Routing.network(store.routes());
      List<Integer> path = Routing.shortestPath(network, from, to);

      pathLabel.setText(path.isEmpty()
          ? "No path found"
          : String.join(" -> ", path.stream().map(store::cityName).toList()));
    } catch (NumberFormatException exception) {
      pathLabel.setText("Enter numeric zone ids");
    } catch (IllegalArgumentException exception) {
      pathLabel.setText(exception.getMessage());
    }
  }
}
