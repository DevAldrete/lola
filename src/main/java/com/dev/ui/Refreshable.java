package com.dev.ui;

/** Implemented by panels that rebuild their view from the {@link Store}. */
public interface Refreshable {
  void refresh();
}
