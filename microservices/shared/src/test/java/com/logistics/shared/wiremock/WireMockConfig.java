package com.logistics.shared.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * WireMock configuration for mocking external service interactions.
 * Provides consistent mocking infrastructure across all microservices tests.
 *
 * Per constitution requirement: Comprehensive testing with external service mocking.
 */
@TestConfiguration
public class WireMockConfig {

    /**
     * WireMock server for mocking external API calls.
     * Configured with dynamic port allocation to avoid conflicts.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Primary
    public WireMockServer wireMockServer() {
        return new WireMockServer(
                WireMockConfiguration.options()
                        .dynamicPort()
                        .usingFilesUnderDirectory("src/test/resources/wiremock")
                        .globalTemplating(true)
        );
    }
}