package com.logistics.shared.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests across all microservices.
 * Provides PostgreSQL and Redis containers for consistent test environments.
 *
 * Per constitution requirement: 80% test coverage with comprehensive integration tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    /**
     * PostgreSQL container for database integration tests.
     * Uses @ServiceConnection for automatic Spring Boot configuration.
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("logistics_test")
                .withUsername("test_user")
                .withPassword("test_password")
                .withReuse(true);
    }

    /**
     * Redis container for caching and session tests.
     * Uses @ServiceConnection for automatic Spring Boot configuration.
     */
    @Bean
    @ServiceConnection
    public GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379)
                .withReuse(true);
    }
}