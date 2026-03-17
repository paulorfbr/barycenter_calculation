package com.logistics.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Dashboard Service entry point.
 *
 * Implements the CQRS read side for the dashboard:
 *   - Consumes domain events from Kafka to maintain a cached read model.
 *   - Exposes a single aggregated GET /api/v1/dashboard endpoint.
 *   - Falls back to fan-out Feign calls when the cache is cold.
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
@EnableKafka
public class DashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardServiceApplication.class, args);
    }
}
