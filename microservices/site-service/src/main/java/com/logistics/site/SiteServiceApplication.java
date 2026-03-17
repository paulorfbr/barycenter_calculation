package com.logistics.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Site Service entry point.
 *
 * Manages ConsumptionSite entities — the geographic and tonnage inputs
 * to barycenter calculations.  Validates company ownership by calling
 * Company Service via Feign.  Publishes site lifecycle events to Kafka
 * so Calculation Service can trigger auto-recalculation.
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
@EnableKafka
public class SiteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteServiceApplication.class, args);
    }
}
