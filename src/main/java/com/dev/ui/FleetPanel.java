package com.dev.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.dev.domain.Vehicle;
import com.dev.ds.BinaryTree;

/** Vehicles kept in a binary search tree and listed from smallest to largest. */
public final class FleetPanel extends JPanel implements Refreshable {

  private static final long serialVersionUID = 1L;

  private final transient Store store;

  private final DefaultTableModel model;
  private final JTextField idField = new JTextField(4);
  private final JTextField plateField = new JTextField(8);
  private final JTextField capacityField = new JTextField(6);
  private final JLabel message = new JLabel(" ");

  public FleetPanel(Store store) {
    this.store = store;

    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    model = new DefaultTableModel(new Object[] { "Id", "Plate", "Capacity (kg)" }, 0) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JTable table = new JTable(model);
    table.setRowHeight(24);
    table.getTableHeader().setReorderingAllowed(false);

    JLabel caption = new JLabel("Ordered by capacity using a binary search tree (in-order walk)");
    caption.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));

    add(caption, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildAddBar(), BorderLayout.SOUTH);

    refresh();
  }

  private JPanel buildAddBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    bar.add(new JLabel("Id:"));
    bar.add(idField);
    bar.add(new JLabel("Plate:"));
    bar.add(plateField);
    bar.add(new JLabel("Capacity (kg):"));
    bar.add(capacityField);

    JButton add = new JButton("Add vehicle");
    add.addActionListener(event -> addVehicle());
    bar.add(add);
    bar.add(message);

    return bar;
  }

  @Override
  public void refresh() {
    BinaryTree<Vehicle> tree = new BinaryTree<>();

    for (Vehicle vehicle : store.vehicles()) {
      tree.push(vehicle);
    }

    model.setRowCount(0);
    for (Vehicle vehicle : tree.asList(BinaryTree.Order.INORDER)) {
      model.addRow(new Object[] { vehicle.id(), vehicle.plate(), vehicle.capacityKg() });
    }
  }

  private void addVehicle() {
    try {
      int id = Integer.parseInt(idField.getText().trim());
      String plate = plateField.getText().trim();
      float capacity = Float.parseFloat(capacityField.getText().trim());

      List<Vehicle> vehicles = new ArrayList<>(store.vehicles());
      vehicles.add(new Vehicle(id, plate, capacity));
      store.setVehicles(vehicles);

      idField.setText("");
      plateField.setText("");
      capacityField.setText("");
      message.setText("Added " + plate);

      refresh();
    } catch (NumberFormatException exception) {
      message.setText("Id and capacity must be numbers");
    } catch (IllegalArgumentException exception) {
      message.setText(exception.getMessage());
    }
  }
}
