package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchProcessingApplicationTests {

  @Test
  void contextLoads() {
    assertEquals("3.0.2", SpringBootVersion.getVersion());
  }
}
