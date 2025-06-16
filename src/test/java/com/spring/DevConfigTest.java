package com.spring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "dev")
public class DevConfigTest {

  @Autowired
  private AppConfig appConfig;

  @Test
  public void test_whenDefaultConfigLoads_thenAllConfigValueIsLoaded() {
    List<String> items = appConfig.getItems();

    assertThat(items.size()).isEqualTo(3);

    assertThat(items.get(0)).isEqualTo("Item 1");
    assertThat(items.get(1)).isEqualTo("Item 6");
    assertThat(items.get(2)).isEqualTo("Item 7");
  }
}
