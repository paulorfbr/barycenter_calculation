package com.logistics.infrastructure.web.controller;

import com.logistics.application.dto.ConsumptionSiteDto;
import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.infrastructure.web.mapper.ConsumptionSiteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for consumption site management.
 *
 * Provides two URL hierarchies to match Angular component expectations:
 *
 *   /api/v1/companies/{companyId}/sites  — company-scoped site access
 *   /api/v1/sites/{siteId}               — direct site access by ID
 *
 * The controller delegates all business logic to ConsumptionSiteService.
 */
@RestController
@Tag(name = "Consumption Sites", description = "Consumption site CRUD within a company's logistics network")
public class ConsumptionSiteController {

    private final ConsumptionSiteService siteService;
    private final ConsumptionSiteMapper  mapper;

    public ConsumptionSiteController(ConsumptionSiteService siteService,
                                     ConsumptionSiteMapper mapper) {
        this.siteService = siteService;
        this.mapper      = mapper;
    }

    // =========================================================================
    // GET /api/v1/companies/{companyId}/sites
    // =========================================================================

    @GetMapping("/api/v1/companies/{companyId}/sites")
    @Operation(summary = "List all consumption sites for a company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sites returned"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public List<ConsumptionSiteDto> listSitesForCompany(
            @Parameter(description = "Company ID", required = true)
            @PathVariable String companyId,

            @Parameter(description = "Filter by status: ACTIVE or INACTIVE")
            @RequestParam(required = false) String status) {

        List<ConsumptionSite> sites = (status != null && status.equalsIgnoreCase("ACTIVE"))
                ? siteService.findActiveByCompany(companyId)
                : siteService.findByCompany(companyId);

        return sites.stream().map(mapper::toDto).toList();
    }

    // =========================================================================
    // GET /api/v1/sites/{id}
    // =========================================================================

    @GetMapping("/api/v1/sites/{id}")
    @Operation(summary = "Get a consumption site by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site found"),
            @ApiResponse(responseCode = "404", description = "Site not found")
    })
    public ConsumptionSiteDto getSite(
            @Parameter(description = "Site UUID", required = true)
            @PathVariable String id) {

        ConsumptionSite site = siteService.findById(id)
                .orElseThrow(() -> new ManageConsumptionSiteUseCase.ConsumptionSiteNotFoundException(id));
        return mapper.toDto(site);
    }

    // =========================================================================
    // POST /api/v1/companies/{companyId}/sites
    // =========================================================================

    @PostMapping("/api/v1/companies/{companyId}/sites")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a consumption site to a company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Site created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ConsumptionSiteDto createSite(
            @PathVariable String companyId,
            @Valid @RequestBody CreateSiteRequest request) {

        ConsumptionSite site = siteService.addSite(
                new ManageConsumptionSiteUseCase.AddSiteCommand(
                        companyId,
                        request.name(),
                        request.description(),
                        request.latitude(),
                        request.longitude(),
                        request.weightTons(),
                        request.address(),
                        request.city(),
                        request.country()));

        return mapper.toDto(site);
    }

    // =========================================================================
    // PUT /api/v1/sites/{id}
    // =========================================================================

    @PutMapping("/api/v1/sites/{id}")
    @Operation(summary = "Update a consumption site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Site not found")
    })
    public ConsumptionSiteDto updateSite(
            @PathVariable String id,
            @Valid @RequestBody UpdateSiteRequest request) {

        ConsumptionSite site = siteService.updateSite(
                new ManageConsumptionSiteUseCase.UpdateSiteCommand(
                        id,
                        request.name(),
                        request.description(),
                        request.latitude(),
                        request.longitude(),
                        request.weightTons(),
                        request.address(),
                        request.city(),
                        request.country()));

        return mapper.toDto(site);
    }

    // =========================================================================
    // DELETE /api/v1/companies/{companyId}/sites/{siteId}
    // =========================================================================

    @DeleteMapping("/api/v1/companies/{companyId}/sites/{siteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a consumption site from a company")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Site removed"),
            @ApiResponse(responseCode = "404", description = "Company or site not found")
    })
    public void deleteSite(
            @PathVariable String companyId,
            @PathVariable String siteId) {

        siteService.removeSite(companyId, siteId);
    }

    // =========================================================================
    // Request body records
    // =========================================================================

    public record CreateSiteRequest(
            @NotBlank(message = "Site name must not be blank")
            String name,

            String description,

            double latitude,
            double longitude,

            @Positive(message = "Weight must be positive")
            double weightTons,

            String address,
            String city,
            String country) {}

    public record UpdateSiteRequest(
            @NotBlank(message = "Site name must not be blank")
            String name,

            String description,

            double latitude,
            double longitude,

            @Positive(message = "Weight must be positive")
            double weightTons,

            String address,
            String city,
            String country) {}
}
