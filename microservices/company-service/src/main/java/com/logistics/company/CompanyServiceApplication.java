package com.logistics.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Company Service entry point.
 *
 * Spring Boot 3.2 auto-configuration handles:
 *   - DataSource + JPA (PostgreSQL)
 *   - Flyway database migrations
 *   - Kafka producer/consumer
 *   - Redis caching
 *   - Actuator endpoints (health, metrics, info)
 *   - Micrometer tracing (Brave / Zipkin)
 */
@SpringBootApplication
@EnableCaching
@EnableKafka
@ConfigurationPropertiesScan
public class CompanyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyServiceApplication.class, args);
    }
}
