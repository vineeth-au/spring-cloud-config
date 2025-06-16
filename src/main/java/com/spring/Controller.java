package com.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  @Autowired
  private AppConfig config;

  @GetMapping("/")
  public String getConfigValues() {
    config.getUsers().forEach(user -> {
      System.out.println("UserName is " + user.getUsername());
      System.out.println("Password is " + user.getPassword());
      System.out.println("Number of Roles assigned is " + user.getRoles().size() + "\n");
    });
    return config.getUsers().toString();
  }
}
