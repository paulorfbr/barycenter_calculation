package com.logistics.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Gateway infrastructure configuration.
 *
 * Registers:
 *   - {@code ipKeyResolver}  — rate limiter key function (keyed by client IP)
 *   - {@code /fallback/**}   — fallback routes returned when a circuit is open
 */
@Configuration
public class GatewayConfiguration {

    /**
     * Rate limiter key resolver — uses the client IP address from the remote
     * address.  Behind a load balancer use {@code X-Forwarded-For} instead.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            var remoteAddress = exchange.getRequest().getRemoteAddress();
            String ip = (remoteAddress != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }

    /**
     * Fallback routes — returned when a downstream circuit breaker opens.
     *
     * Returns a structured JSON error response with HTTP 503 so the Angular
     * SPA can display a meaningful "Service temporarily unavailable" message.
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRoutes() {
        return RouterFunctions.route()
                .GET("/fallback/service-unavailable",  req -> unavailableResponse())
                .POST("/fallback/service-unavailable", req -> unavailableResponse())
                .build();
    }

    private static Mono<ServerResponse> unavailableResponse() {
        Map<String, Object> body = Map.of(
                "timestamp",  Instant.now().toString(),
                "status",     503,
                "error",      "Service Unavailable",
                "message",    "The requested service is temporarily unavailable. Please try again in a few moments.",
                "path",       "/fallback/service-unavailable"
        );
        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .bodyValue(body);
    }
}
