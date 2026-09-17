package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.dev.domain.DeliveryStatus;
import com.dev.domain.Package;
import com.dev.domain.Priority;
import com.dev.ds.HashMap;
import com.dev.modules.Deliveries;

/** Package table with waybill tracking, urgency view and status transitions. */
public final class DeliveriesPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final DefaultTableModel model;
  private final JTable table;
  private final JTextField waybillField = new JTextField(14);
  private final JComboBox<String> statusFilter = new JComboBox<>();
  private final JComboBox<String> priorityFilter = new JComboBox<>();
  private final JSpinner urgentCount = new JSpinner(new SpinnerNumberModel(3, 0, 100, 1));
  private final JLabel message = new JLabel(" ");

  private List<Package> displayed = List.of();

  public DeliveriesPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    model = new DefaultTableModel(
        new Object[] { "Id", "Waybill", "Route", "Weight (kg)", "Price", "Priority", "Status" }, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    table = new JTable(model);
    table.setRowHeight(24);
    table.getTableHeader().setReorderingAllowed(false);

    add(buildFilters(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildActions(), BorderLayout.SOUTH);

    refresh();
  }

  private JPanel buildFilters() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    bar.add(new JLabel("Waybill:"));
    bar.add(waybillField);

    JButton track = new JButton("Track");
    track.addActionListener(event -> track());
    bar.add(track);

    bar.add(new JLabel("Status:"));
    statusFilter.addItem("All");
    for (DeliveryStatus status : DeliveryStatus.values()) {
      statusFilter.addItem(status.name());
    }
    statusFilter.addActionListener(event -> refresh());
    bar.add(statusFilter);

    bar.add(new JLabel("Priority:"));
    priorityFilter.addItem("All");
    for (Priority priority : Priority.values()) {
      priorityFilter.addItem(priority.name());
    }
    priorityFilter.addActionListener(event -> refresh());
    bar.add(priorityFilter);

    JButton clear = new JButton("Clear");
    clear.addActionListener(event -> {
      waybillField.setText("");
      statusFilter.setSelectedItem("All");
      priorityFilter.setSelectedItem("All");
      message.setText(" ");
      refresh();
    });
    bar.add(clear);

    return bar;
  }

  private JPanel buildActions() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    bar.add(new JLabel("Urgent top:"));
    bar.add(urgentCount);

    JButton urgent = new JButton("Show urgent");
    urgent.addActionListener(event -> showUrgent());
    bar.add(urgent);

    JButton advance = new JButton("Advance status");
    advance.addActionListener(event -> advanceSelected());
    bar.add(advance);

    JButton cancel = new JButton("Cancel selected");
    cancel.addActionListener(event -> setSelectedStatus(DeliveryStatus.CANCELED));
    bar.add(cancel);

    bar.add(message);

    return bar;
  }

  @Override
  public void refresh() {
    displayed = visiblePackages();

    model.setRowCount(0);
    for (Package pkg : displayed) {
      model.addRow(new Object[] {
          pkg.id(),
          pkg.waybill(),
          pkg.routeId(),
          pkg.weight(),
          Format.money(pkg.priceInCents()),
          pkg.priority(),
          pkg.status() });
    }
  }

  private List<Package> visiblePackages() {
    List<Package> filtered = new ArrayList<>(store.packages());

    String status = (String) statusFilter.getSelectedItem();
    if (status != null && !"All".equals(status)) {
      filtered = new ArrayList<>(Deliveries.filterByStatus(filtered, DeliveryStatus.valueOf(status)));
    }

    String priority = (String) priorityFilter.getSelectedItem();
    if (priority != null && !"All".equals(priority)) {
      filtered = new ArrayList<>(Deliveries.filterByPriority(filtered, Priority.valueOf(priority)));
    }

    return filtered;
  }

  private void track() {
    String waybill = waybillField.getText().trim();

    if (waybill.isEmpty()) {
      message.setText("Enter a waybill to track");
      return;
    }

    HashMap<String, Package> index = Deliveries.indexByWaybill(store.packages());

    var found = Deliveries.findByWaybill(index, waybill);
    if (found.isEmpty()) {
      message.setText("Not found: " + waybill);
      return;
    }

    selectByWaybill(waybill);
    message.setText("Found " + found.get().waybill() + " (" + found.get().status() + ")");
  }

  private void selectByWaybill(String waybill) {
    for (int row = 0; row < displayed.size(); row++) {
      if (displayed.get(row).waybill().equals(waybill)) {
        table.setRowSelectionInterval(row, row);
        return;
      }
    }
    message.setText(waybill + " is hidden by the current filters");
  }

  private void showUrgent() {
    int count = (int) urgentCount.getValue();
    List<Package> urgent = Deliveries.urgent(store.packages(), count);

    StringBuilder text = new StringBuilder();
    int position = 1;

    for (Package pkg : urgent) {
      text.append(position++).append(". ")
          .append(pkg.waybill())
          .append("   [").append(pkg.priority()).append("]")
          .append("   ").append(pkg.status())
          .append('\n');
    }

    if (urgent.isEmpty()) {
      text.append("No packages to show.");
    }

    JOptionPane.showMessageDialog(this, text.toString(), "Urgent deliveries",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void advanceSelected() {
    Package pkg = selectedPackage();

    if (pkg == null) {
      message.setText("Select a package first");
      return;
    }

    DeliveryStatus next = nextStatus(pkg.status());

    if (next == pkg.status()) {
      message.setText(pkg.waybill() + " is already " + next);
      return;
    }

    setSelectedStatus(next);
  }

  private void setSelectedStatus(DeliveryStatus status) {
    Package pkg = selectedPackage();

    if (pkg == null) {
      message.setText("Select a package first");
      return;
    }

    store.setPackages(Deliveries.updateStatus(store.packages(), pkg.waybill(), status));
    refresh();
    selectByWaybill(pkg.waybill());
    message.setText(pkg.waybill() + " -> " + status);
  }

  private Package selectedPackage() {
    int row = table.getSelectedRow();

    return row < 0 || row >= displayed.size() ? null : displayed.get(row);
  }

  private static DeliveryStatus nextStatus(DeliveryStatus status) {
    return switch (status) {
      case CREATED -> DeliveryStatus.DISPATCHED;
      case DISPATCHED -> DeliveryStatus.IN_TRANSIT;
      case IN_TRANSIT -> DeliveryStatus.DELIVERED;
      case DELIVERED, CANCELED -> status;
    };
  }
}
