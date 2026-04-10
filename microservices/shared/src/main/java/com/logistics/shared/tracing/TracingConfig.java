package com.logistics.shared.tracing;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Distributed tracing configuration for microservices observability.
 * Per constitution requirement: Comprehensive observability with distributed tracing.
 */
@Configuration
@ConditionalOnProperty(prefix = "logistics.tracing", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TracingConfig {

    /**
     * Custom tracing service for business operations.
     */
    @Bean
    public TracingService tracingService(Tracer tracer) {
        return new TracingService(tracer);
    }
}