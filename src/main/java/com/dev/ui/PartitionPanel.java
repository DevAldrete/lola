package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.dev.modules.Routing;

/** Geographic partitioning: spread the delivery batch across the fleet. */
public final class PartitionPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final DefaultTableModel model;
  private final JLabel summary = new JLabel(" ");

  public PartitionPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    model = new DefaultTableModel(
        new Object[] { "Vehículo", "Placa", "Capacidad", "Paquetes", "Peso asignado", "Uso" }, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(24);
    table.getTableHeader().setReorderingAllowed(false);

    JPanel header = new JPanel(new BorderLayout());
    header.setBorder(BorderFactory.createTitledBorder("Partición de entregas por vehículo"));
    header.add(new JLabel("Divide y vencerás: divide el lote y equilibra la carga por capacidad."),
        BorderLayout.NORTH);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    JButton partition = new JButton("Particionar entregas");
    partition.addActionListener(event -> refresh());
    actions.add(partition);
    actions.add(summary);
    header.add(actions, BorderLayout.SOUTH);

    add(header, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);

    refresh();
  }

  @Override
  public void refresh() {
    var assignments = Routing.partitionDeliveries(store.packages(), store.vehicles());

    model.setRowCount(0);

    int packages = 0;
    float weight = 0f;

    for (Routing.Assignment assignment : assignments) {
      float capacity = assignment.vehicle().capacityKg();
      float assigned = assignment.totalWeight();
      float usage = capacity == 0 ? 0f : assigned / capacity * 100f;

      packages += assignment.packages().size();
      weight += assigned;

      model.addRow(new Object[] {
          "#" + assignment.vehicle().id(),
          assignment.vehicle().plate(),
          Format.weight(capacity),
          assignment.packages().size(),
          Format.weight(assigned),
          String.format("%.0f%%", usage) });
    }

    summary.setText("Vehículos usados: " + assignments.size()
        + " | Paquetes: " + packages
        + " | Peso total: " + Format.weight(weight));
  }
}
