package com.logistics.shared.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for tests requiring external service mocking with WireMock.
 *
 * Usage:
 * <pre>
 * {@code @SpringBootTest}
 * class MyServiceTest extends BaseWireMockTest {
 *     {@code @Test}
 *     void testExternalApiCall() {
 *         stubFor(get(urlEqualTo("/api/external"))
 *             .willReturn(aResponse()
 *                 .withStatus(200)
 *                 .withBody("mocked response")));
 *         // Test code that calls external API
 *     }
 * }
 * </pre>
 *
 * Per constitution requirement: Mock external dependencies for reliable testing.
 */
@SpringBootTest
@ContextConfiguration(classes = WireMockConfig.class)
public abstract class BaseWireMockTest {

    @Autowired
    protected WireMockServer wireMockServer;

    @BeforeEach
    void setUpWireMock() {
        WireMock.configureFor("localhost", wireMockServer.port());
        wireMockServer.resetAll();
    }

    /**
     * Helper method to get the base URL for mocked services.
     * Use this in your test setup to configure service clients.
     */
    protected String getWireMockBaseUrl() {
        return String.format("http://localhost:%d", wireMockServer.port());
    }
}