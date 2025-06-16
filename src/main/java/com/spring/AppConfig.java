package com.spring;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "my-application")
public class AppConfig {

  private List<Users> users;

  @Getter
  @Setter
  @ToString
  public static class Users {

    private String username;
    private String password;
    private List<String> roles;
  }
}
