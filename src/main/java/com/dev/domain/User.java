package com.dev.domain;

import java.util.Objects;

public record User(int id, String name, String email, String password, Role role) {
  public User {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(email, "email must not be null");
    Objects.requireNonNull(password, "password must not be null");
    Objects.requireNonNull(role, "password must not be null");
  }

  public User withRole(Role role) {
    return new User(this.id, this.name, this.email, this.password, role);
  }
}
