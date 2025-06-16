package com.spring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.spring.AppConfig.Users;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class DefaultConfigTest {

  @Autowired
  private AppConfig appConfig;

  @Test
  public void test_whenDefaultConfigLoads_thenAllConfigValueIsLoaded() {
    List<Users> users = appConfig.getUsers();

    assertThat(users.size()).isEqualTo(3);
    assertThat(users.get(0).getRoles().size()).isEqualTo(3);

    assertThat(users.get(0).getUsername()).isEqualTo("admin");
    assertThat(users.get(1).getUsername()).isEqualTo("dev");
    assertThat(users.get(2).getUsername()).isEqualTo("staging");
  }
}
