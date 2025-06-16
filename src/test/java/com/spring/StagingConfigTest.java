package com.spring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.spring.AppConfig.Users;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "staging")
public class StagingConfigTest {

  @Autowired
  private AppConfig appConfig;

  @Test
  public void test_whenDefaultConfigLoads_thenAllConfigValueIsLoaded() {
    List<Users> users = appConfig.getUsers();

    assertThat(users.size()).isEqualTo(1);
    assertThat(users.get(0).getRoles().size()).isEqualTo(1);

    assertThat(users.get(0).getUsername()).isEqualTo("staging");
    assertThat(users.get(0).getPassword()).isEqualTo("stagingPassword");
    assertThat(users.get(0).getRoles().get(0)).isEqualTo("WRITE");
  }
}
