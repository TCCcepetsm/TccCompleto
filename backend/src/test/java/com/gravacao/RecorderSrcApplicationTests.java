package com.gravacao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

// Para usar com H2 (sem Docker) - remova @Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class RecorderSrcApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void testDatabaseConnection() {
        assertThat(context.getEnvironment().getProperty("spring.datasource.url"))
                .isNotNull();
    }
}