package com.logistics.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway entry point.
 *
 * Spring Cloud Gateway runs on Project Reactor (non-blocking / reactive).
 * All routing, filtering, and rate-limiting configuration lives in
 * {@code application.yml} using the declarative routes DSL — no Java
 * route configuration is needed for the standard proxy use case.
 *
 * Cross-cutting concerns handled here:
 *   - CORS policy (allowed origins, methods, headers)
 *   - Rate limiting (Redis token bucket, 100 req/s per IP by default)
 *   - Request ID injection (X-Request-Id header)
 *   - Response time header (X-Response-Time-Ms)
 *   - Distributed trace context propagation (B3 headers via Micrometer Tracing)
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
