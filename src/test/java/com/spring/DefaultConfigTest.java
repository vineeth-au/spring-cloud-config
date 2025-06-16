package com.spring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.spring.AppConfig.Users;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "default")
public class DefaultConfigTest {

  @Autowired
  private AppConfig appConfig;

  @Test
  public void test_whenDefaultConfigLoads_thenAllConfigValueIsLoaded() {
    List<String> items = appConfig.getItems();

    assertThat(items.size()).isEqualTo(5);

    assertThat(items.get(0)).isEqualTo("Item 1");
    assertThat(items.get(1)).isEqualTo("Item 2");
    assertThat(items.get(2)).isEqualTo("Item 3");
    assertThat(items.get(3)).isEqualTo("Item 4");
    assertThat(items.get(4)).isEqualTo("Item 5");
  }
}
