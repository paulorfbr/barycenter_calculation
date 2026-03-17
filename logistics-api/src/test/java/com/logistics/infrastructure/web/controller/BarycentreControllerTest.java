package com.logistics.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.application.dto.BarycentreResultDto;
import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;
import com.logistics.infrastructure.web.mapper.BarycentreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.logistics.infrastructure.config.LogisticsProperties;
import com.logistics.infrastructure.config.WebConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link BarycentreController}.
 */
@WebMvcTest(controllers = BarycentreController.class)
@Import(WebConfig.class)
@EnableConfigurationProperties(LogisticsProperties.class)
@TestPropertySource(properties = {
        "logistics.seed-data.enabled=false",
        "logistics.cors.allowed-origins=http://localhost:4200"
})
@DisplayName("BarycentreController")
class BarycentreControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean BarycentreService          barycentreService;
    @MockBean ConsumptionSiteService     siteService;
    @MockBean LogisticsCenterRepository  centerRepository;
    @MockBean BarycentreMapper           mapper;

    private LogisticsCenter sampleCenter;
    private BarycentreResultDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleCenter = new LogisticsCenter(
                "lc-001", "cmp-001", "Test Center",
                new GeoCoordinate(38.5, -90.0),
                TrafficVolume.ofTons(1000.0),
                List.of("site-1", "site-2"),
                "weighted-barycenter", 1, 0.0);

        sampleDto = new BarycentreResultDto(
                "lc-001", "cmp-001",
                38.5, -90.0, "38.5000, -90.0000",
                "weighted-barycenter", 1, 0.0,
                1000.0, 2,
                List.of(),
                "CANDIDATE");
    }

    // =========================================================================
    // POST /api/v1/barycentre/calculate
    // =========================================================================

    @Nested
    @DisplayName("POST /api/v1/barycentre/calculate")
    class Calculate {

        @Test
        @DisplayName("returns 201 with result when calculation succeeds")
        void returnsResultOnSuccess() throws Exception {
            when(barycentreService.calculateForCompany(any())).thenReturn(sampleCenter);
            when(siteService.findById(any())).thenReturn(Optional.empty());
            when(mapper.toDto(any(), anyList())).thenReturn(sampleDto);

            String body = """
                    {
                        "companyId": "cmp-001",
                        "algorithm": "weighted-barycenter"
                    }
                    """;

            mockMvc.perform(post("/api/v1/barycentre/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.logisticsCenterId", is("lc-001")))
                    .andExpect(jsonPath("$.optimalLatitude", is(38.5)))
                    .andExpect(jsonPath("$.algorithmDescription", is("weighted-barycenter")));
        }

        @Test
        @DisplayName("returns 400 when company has insufficient sites")
        void returns400OnInsufficientSites() throws Exception {
            when(barycentreService.calculateForCompany(any()))
                    .thenThrow(new CalculateBarycentreUseCase.InsufficientSitesException("cmp-001", 1));

            String body = """
                    {
                        "companyId": "cmp-001",
                        "algorithm": "weighted-barycenter"
                    }
                    """;

            mockMvc.perform(post("/api/v1/barycentre/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is("Insufficient Sites")));
        }

        @Test
        @DisplayName("returns 422 when companyId is blank")
        void returns422WhenCompanyIdBlank() throws Exception {
            String body = """
                    {
                        "companyId": "",
                        "algorithm": "weighted-barycenter"
                    }
                    """;

            mockMvc.perform(post("/api/v1/barycentre/calculate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // =========================================================================
    // POST /api/v1/barycentre/calculate/preview
    // =========================================================================

    @Nested
    @DisplayName("POST /api/v1/barycentre/calculate/preview")
    class Preview {

        @Test
        @DisplayName("returns 201 for valid inline site data")
        void returnsResultForInlineData() throws Exception {
            when(barycentreService.calculate(any())).thenReturn(sampleCenter);
            when(mapper.toDto(any(), anyList())).thenReturn(sampleDto);

            String body = """
                    {
                        "algorithm": "weighted-barycenter",
                        "sites": [
                            { "name": "A", "latitude": 34.05, "longitude": -118.24, "weightTons": 100 },
                            { "name": "B", "latitude": 40.71, "longitude": -74.00,  "weightTons": 200 }
                        ]
                    }
                    """;

            mockMvc.perform(post("/api/v1/barycentre/calculate/preview")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.logisticsCenterId", is("lc-001")));
        }
    }

    // =========================================================================
    // GET /api/v1/barycentre/{companyId}/results
    // =========================================================================

    @Nested
    @DisplayName("GET /api/v1/barycentre/{companyId}/results")
    class GetResults {

        @Test
        @DisplayName("returns 200 with list of results")
        void returnsResults() throws Exception {
            when(centerRepository.findByCompanyId("cmp-001")).thenReturn(List.of(sampleCenter));
            when(siteService.findById(any())).thenReturn(Optional.empty());
            when(mapper.toDto(any(), anyList())).thenReturn(sampleDto);

            mockMvc.perform(get("/api/v1/barycentre/cmp-001/results")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].logisticsCenterId", is("lc-001")));
        }
    }
}
