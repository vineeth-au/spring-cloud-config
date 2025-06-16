package com.spring;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ToString
@ConfigurationProperties(prefix = "my-application")
public class AppConfig {

  private List<String> items;

  @PostConstruct
  public void validateConfig() {
    System.out.println("App Config {}" + toString());
  }
}
