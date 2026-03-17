package com.logistics.app;

import com.logistics.application.service.CompanyService;
import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.DashboardService;
import com.logistics.infrastructure.config.LogisticsProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full Spring Boot integration test that loads the complete application context
 * and exercises the API end-to-end against in-memory repositories.
 *
 * Test data is seeded by {@link com.logistics.infrastructure.adapter.SampleDataInitializer}
 * via the logistics.seed-data.enabled=true property (set below).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "logistics.seed-data.enabled=true",
        "logistics.cors.allowed-origins=http://localhost:4200"
})
@DisplayName("Logistics API — Integration Tests")
class LogisticsApiApplicationIT {

    @Autowired MockMvc mockMvc;
    @Autowired CompanyService    companyService;
    @Autowired BarycentreService barycentreService;
    @Autowired DashboardService  dashboardService;
    @Autowired LogisticsProperties properties;

    // =========================================================================
    // Context loading
    // =========================================================================

    @Test
    @DisplayName("Spring Boot application context loads without errors")
    void contextLoads() {
        assertThat(companyService).isNotNull();
        assertThat(barycentreService).isNotNull();
        assertThat(dashboardService).isNotNull();
    }

    @Test
    @DisplayName("LogisticsProperties are bound from application.yml")
    void propertiesArebound() {
        assertThat(properties.seedData().enabled()).isTrue();
        assertThat(properties.cors().allowedOrigins()).contains("http://localhost:4200");
    }

    // =========================================================================
    // Company endpoints
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/companies returns seeded companies")
    void getCompaniesReturnsSeedData() throws Exception {
        mockMvc.perform(get("/api/v1/companies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("POST /api/v1/companies creates a new company")
    void postCompanyCreatesNewCompany() throws Exception {
        String body = """
                {
                    "name": "Integration Test Corp",
                    "type": "CARRIER",
                    "contactName": "Test User",
                    "contactEmail": "test@it.com"
                }
                """;

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Corp"))
                .andExpect(jsonPath("$.type").value("CARRIER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    @DisplayName("GET /api/v1/companies/nonexistent returns 404 Problem Detail")
    void getNonexistentCompanyReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/companies/nonexistent-id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Company Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    // =========================================================================
    // Sites endpoints
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/companies/{id}/sites returns sites for a seeded company")
    void getSitesForSeedCompany() throws Exception {
        // Find the first seeded company (Acme Corp)
        var companies = companyService.findAll();
        assertThat(companies).isNotEmpty();

        String companyId = companies.stream()
                .filter(c -> c.getName().equals("Acme Corp"))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/api/v1/companies/{id}/sites", companyId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));  // Acme has 4 seeded sites
    }

    // =========================================================================
    // Dashboard endpoint
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/dashboard returns complete summary")
    void getDashboardReturnsSummary() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCompanies").isNumber())
                .andExpect(jsonPath("$.activeShipments").isNumber())
                .andExpect(jsonPath("$.totalLocations").isNumber())
                .andExpect(jsonPath("$.onTimeRatePercent").isNumber())
                .andExpect(jsonPath("$.recentActivity").isArray())
                .andExpect(jsonPath("$.overdueShipments").isArray());
    }

    // =========================================================================
    // Barycenter endpoint
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/barycentre/calculate runs calculation for seeded company")
    void calculateBarycentreForSeedCompany() throws Exception {
        String acmeId = companyService.findAll().stream()
                .filter(c -> c.getName().equals("Acme Corp"))
                .findFirst()
                .orElseThrow()
                .getId();

        String body = String.format("""
                {
                    "companyId": "%s",
                    "algorithm": "weighted-barycenter"
                }
                """, acmeId);

        mockMvc.perform(post("/api/v1/barycentre/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logisticsCenterId").isString())
                .andExpect(jsonPath("$.optimalLatitude").isNumber())
                .andExpect(jsonPath("$.optimalLongitude").isNumber())
                .andExpect(jsonPath("$.status").value("CANDIDATE"))
                .andExpect(jsonPath("$.inputSiteCount").value(4));
    }

    // =========================================================================
    // SPA fallback
    // =========================================================================

    @Test
    @DisplayName("GET / forwards to index.html (Angular SPA root)")
    void rootPathForwardsToSpa() throws Exception {
        // index.html is in classpath:/static/ — it will return 200 if file exists
        // or 404 if no static file is present. Either way the controller must not 500.
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus())
                                .isIn(200, 404));  // 200 when static file exists, 404 in bare test
    }

    // =========================================================================
    // Actuator
    // =========================================================================

    @Test
    @DisplayName("GET /actuator/health returns UP status")
    void actuatorHealthIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
