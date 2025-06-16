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
    config.getItems().forEach(x-> {
      System.out.println("Item "+ x);
    });
    return config.getItems().toString();
  }
}
