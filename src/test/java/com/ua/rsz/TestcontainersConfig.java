package com.ua.rsz;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Starts a real PostgreSQL in Docker for tests.
 * @ServiceConnection tells Spring Boot to use this container as the datasource,
 * so we do not need to set the url, user and password by hand.
 * Add it to a test with @Import(TestcontainersConfig.class).
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
