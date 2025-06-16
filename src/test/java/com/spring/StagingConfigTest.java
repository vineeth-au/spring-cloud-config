package com.spring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    List<String> items = appConfig.getItems();

    assertThat(items.size()).isEqualTo(1);

    assertThat(items.get(0)).isEqualTo("Item 8");
  }
}
