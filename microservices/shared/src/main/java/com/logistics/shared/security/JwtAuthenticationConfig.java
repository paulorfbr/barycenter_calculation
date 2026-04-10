package com.logistics.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

/**
 * JWT authentication configuration properties.
 * Per constitution requirement: Enterprise-grade security with JWT authentication.
 */
@ConfigurationProperties(prefix = "logistics.security.jwt")
@Validated
public record JwtAuthenticationConfig(
        @NotBlank String secretKey,
        @NotNull Duration accessTokenExpiry,
        @NotNull Duration refreshTokenExpiry,
        @NotBlank String issuer,
        @NotBlank String audience
) {
    public static final String DEFAULT_ISSUER = "logistics-platform";
    public static final String DEFAULT_AUDIENCE = "logistics-services";
    public static final Duration DEFAULT_ACCESS_TOKEN_EXPIRY = Duration.ofHours(1);
    public static final Duration DEFAULT_REFRESH_TOKEN_EXPIRY = Duration.ofDays(7);
}