package com.logistics.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.application.dto.CompanyDto;
import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.service.CompanyService;
import com.logistics.domain.model.Company;
import com.logistics.infrastructure.web.mapper.CompanyMapper;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Spring MVC slice test for {@link CompanyController}.
 *
 * Uses @WebMvcTest to load only the web layer. The CompanyService and CompanyMapper
 * are replaced with Mockito mocks to isolate controller behaviour.
 */
@WebMvcTest(controllers = CompanyController.class)
@Import(WebConfig.class)
@EnableConfigurationProperties(LogisticsProperties.class)
@TestPropertySource(properties = {
        "logistics.seed-data.enabled=false",
        "logistics.cors.allowed-origins=http://localhost:4200"
})
@DisplayName("CompanyController")
class CompanyControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CompanyService companyService;
    @MockBean CompanyMapper  companyMapper;

    private CompanyDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new CompanyDto(
                "cmp-001", "Acme Corp", "SHIPPER", "ACTIVE",
                "12-3456789", "Jane Smith", "j@acme.com", "+1-555-0100",
                null, 4, 1205.0,
                "2024-01-15T09:00:00Z", "2024-03-01T14:30:00Z");
    }

    // =========================================================================
    // GET /api/v1/companies
    // =========================================================================

    @Nested
    @DisplayName("GET /api/v1/companies")
    class ListCompanies {

        @Test
        @DisplayName("returns 200 with a list of companies")
        void returnsCompanyList() throws Exception {
            Company company = new Company("cmp-001", "Acme Corp",
                    Company.Type.SHIPPER, Company.Status.ACTIVE);
            when(companyService.findAll()).thenReturn(List.of(company));
            when(companyMapper.toDto(company)).thenReturn(sampleDto);

            mockMvc.perform(get("/api/v1/companies").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is("cmp-001")))
                    .andExpect(jsonPath("$[0].name", is("Acme Corp")))
                    .andExpect(jsonPath("$[0].type", is("SHIPPER")))
                    .andExpect(jsonPath("$[0].status", is("ACTIVE")));
        }

        @Test
        @DisplayName("filters by status query parameter")
        void filtersCompanyByStatus() throws Exception {
            Company company = new Company("cmp-001", "Acme Corp",
                    Company.Type.SHIPPER, Company.Status.ACTIVE);
            when(companyService.findByStatus(Company.Status.ACTIVE))
                    .thenReturn(List.of(company));
            when(companyMapper.toDto(company)).thenReturn(sampleDto);

            mockMvc.perform(get("/api/v1/companies")
                            .param("status", "ACTIVE")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(companyService).findByStatus(Company.Status.ACTIVE);
            verify(companyService, never()).findAll();
        }

        @Test
        @DisplayName("returns empty list when no companies exist")
        void returnsEmptyList() throws Exception {
            when(companyService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/companies").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // =========================================================================
    // GET /api/v1/companies/{id}
    // =========================================================================

    @Nested
    @DisplayName("GET /api/v1/companies/{id}")
    class GetCompany {

        @Test
        @DisplayName("returns 200 with company when found")
        void returnsCompanyById() throws Exception {
            Company company = new Company("cmp-001", "Acme Corp",
                    Company.Type.SHIPPER, Company.Status.ACTIVE);
            when(companyService.findById("cmp-001")).thenReturn(Optional.of(company));
            when(companyMapper.toDto(company)).thenReturn(sampleDto);

            mockMvc.perform(get("/api/v1/companies/cmp-001").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("cmp-001")))
                    .andExpect(jsonPath("$.name", is("Acme Corp")));
        }

        @Test
        @DisplayName("returns 404 when company does not exist")
        void returns404WhenNotFound() throws Exception {
            when(companyService.findById("unknown"))
                    .thenThrow(new ManageCompanyUseCase.CompanyNotFoundException("unknown"));

            mockMvc.perform(get("/api/v1/companies/unknown").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is("Company Not Found")));
        }
    }

    // =========================================================================
    // POST /api/v1/companies
    // =========================================================================

    @Nested
    @DisplayName("POST /api/v1/companies")
    class CreateCompany {

        @Test
        @DisplayName("returns 201 with created company on valid request")
        void createsCompany() throws Exception {
            Company company = new Company("cmp-new", "New Corp",
                    Company.Type.CARRIER, Company.Status.ACTIVE);
            CompanyDto newDto = new CompanyDto(
                    "cmp-new", "New Corp", "CARRIER", "ACTIVE",
                    null, null, null, null, null, 0, 0.0,
                    Instant.now().toString(), Instant.now().toString());

            when(companyService.createCompany(any())).thenReturn(company);
            when(companyMapper.toDto(company)).thenReturn(newDto);

            String body = """
                    {
                        "name": "New Corp",
                        "type": "CARRIER"
                    }
                    """;

            mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("cmp-new")))
                    .andExpect(jsonPath("$.name", is("New Corp")));
        }

        @Test
        @DisplayName("returns 422 when name is blank")
        void rejects422WhenNameBlank() throws Exception {
            String body = """
                    {
                        "name": "",
                        "type": "CARRIER"
                    }
                    """;

            mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.fieldErrors.name", notNullValue()));
        }

        @Test
        @DisplayName("returns 422 when type is missing")
        void rejects422WhenTypeMissing() throws Exception {
            String body = """
                    {
                        "name": "Valid Corp"
                    }
                    """;

            mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // =========================================================================
    // DELETE /api/v1/companies/{id}
    // =========================================================================

    @Nested
    @DisplayName("DELETE /api/v1/companies/{id}")
    class DeleteCompany {

        @Test
        @DisplayName("returns 204 when company is deleted")
        void deletesCompany() throws Exception {
            doNothing().when(companyService).deleteCompany("cmp-001");

            mockMvc.perform(delete("/api/v1/companies/cmp-001"))
                    .andExpect(status().isNoContent());

            verify(companyService).deleteCompany("cmp-001");
        }

        @Test
        @DisplayName("returns 404 when company not found")
        void returns404WhenNotFound() throws Exception {
            doThrow(new ManageCompanyUseCase.CompanyNotFoundException("unknown"))
                    .when(companyService).deleteCompany("unknown");

            mockMvc.perform(delete("/api/v1/companies/unknown"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // PATCH /api/v1/companies/{id}/activate
    // =========================================================================

    @Nested
    @DisplayName("PATCH /api/v1/companies/{id}/activate")
    class ActivateCompany {

        @Test
        @DisplayName("returns 204 on successful activation")
        void activatesCompany() throws Exception {
            doNothing().when(companyService).activateCompany("cmp-001");

            mockMvc.perform(patch("/api/v1/companies/cmp-001/activate"))
                    .andExpect(status().isNoContent());

            verify(companyService).activateCompany("cmp-001");
        }
    }
}
