package com.dev.lola;

import java.time.Duration;

import com.dev.domain.Role;
import com.dev.domain.Route;
import com.dev.domain.User;

public class App {
  public static void main(String[] args) {
    User myUser = new User(100, "The User", "user@test.com", "test", Role.ADMIN);
    Route myRoute = new Route(777, 42, 67, 20450, Duration.ofDays(7), 350);
    System.out.println("User: " + myUser);
    System.out.println("Route: " + myRoute);
  }
}
