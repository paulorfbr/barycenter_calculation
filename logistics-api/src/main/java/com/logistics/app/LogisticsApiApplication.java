package com.logistics.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot application entry point for the Logistics Barycenter API.
 *
 * Architecture overview:
 *
 *   Hexagonal (Ports and Adapters) architecture:
 *
 *   Driving adapters (in):
 *     com.logistics.infrastructure.web.controller  — REST controllers
 *
 *   Application core:
 *     com.logistics.application.port.in            — inbound use-case interfaces
 *     com.logistics.application.port.out           — outbound repository interfaces
 *     com.logistics.application.service            — use-case implementations
 *
 *   Domain:
 *     com.logistics.domain.model                   — aggregates and entities
 *     com.logistics.domain.vo                      — value objects
 *     com.logistics.domain.event                   — domain events
 *
 *   Driven adapters (out):
 *     com.logistics.infrastructure.persistence     — in-memory repositories
 *     com.logistics.infrastructure.calculation     — barycenter engine
 *
 * Static content:
 *   The compiled Angular SPA is copied to src/main/resources/static/ during
 *   the build. Spring Boot's ResourceHttpRequestHandler serves it from there.
 *   The SpaFallbackController handles HTML5 history-mode routing by forwarding
 *   all unmatched non-API paths back to index.html.
 *
 * Spring component scanning starts from the {@code com.logistics} root package,
 * which covers all sub-packages including the domain, application, and
 * infrastructure layers.
 */
@SpringBootApplication(scanBasePackages = "com.logistics")
@ConfigurationPropertiesScan("com.logistics.infrastructure.config")
public class LogisticsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsApiApplication.class, args);
    }
}
