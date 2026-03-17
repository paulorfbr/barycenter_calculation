package com.logistics.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Calculation Service entry point.
 *
 * Feign clients are enabled for synchronous REST calls to Site Service.
 * Kafka is enabled for consuming site-change events and publishing
 * calculation results.
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class CalculationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculationServiceApplication.class, args);
    }
}
