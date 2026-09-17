package com.dev.lola;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.dev.ui.MainFrame;
import com.dev.ui.Store;

public class App {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      useSystemLookAndFeel();
      new MainFrame(new Store()).setVisible(true);
    });
  }

  private static void useSystemLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exception) {
      // Keep the default look and feel when the system one is unavailable.
    }
  }
}
