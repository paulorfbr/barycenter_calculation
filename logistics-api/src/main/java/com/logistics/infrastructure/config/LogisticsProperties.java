package com.logistics.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Strongly-typed configuration properties for the logistics application.
 * Bound from the {@code logistics} prefix in application.yml.
 *
 * Example:
 * <pre>
 * logistics:
 *   seed-data:
 *     enabled: true
 *   cors:
 *     allowed-origins:
 *       - "http://localhost:4200"
 * </pre>
 */
@ConfigurationProperties(prefix = "logistics")
public record LogisticsProperties(
        SeedDataProperties seedData,
        CorsProperties cors) {

    public record SeedDataProperties(boolean enabled) {}

    public record CorsProperties(List<String> allowedOrigins) {}
}
