package com.logistics.shared.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for integration tests that require database and Redis containers.
 *
 * Usage:
 * <pre>
 * {@code @SpringBootTest}
 * class MyServiceIntegrationTest extends BaseIntegrationTest {
 *     // Test methods with full database and Redis support
 * }
 * </pre>
 *
 * Per constitution requirement: Comprehensive integration testing for all services.
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestContainerConfig.class)
public abstract class BaseIntegrationTest {

    /**
     * Verify that TestContainers configuration loads successfully.
     * This test ensures the container setup is working for all extending classes.
     */
    @Test
    void contextLoads() {
        // Context loading with TestContainers validates container startup
    }
}