package com.logistics.infrastructure.web.controller;

import com.logistics.application.dto.BarycentreResultDto;
import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.in.ManageConsumptionSiteUseCase;
import com.logistics.application.port.out.LogisticsCenterRepository;
import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.ConsumptionSiteService;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.infrastructure.web.mapper.BarycentreMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for barycenter calculation operations.
 *
 * Exposes two calculation strategies matching the Angular CalculateRequest model:
 *
 *   POST /api/v1/barycentre/calculate
 *     Calculates using the stored active consumption sites for a given company.
 *     Equivalent to StoredSitesCommand in the use case.
 *
 *   POST /api/v1/barycentre/calculate/preview
 *     Calculates from inline site data without persisting sites first.
 *     Equivalent to SimpleCalculateCommand — used by the quick-preview panel.
 *
 *   GET /api/v1/barycentre/{companyId}/results
 *     Returns all saved calculation results for a company.
 *
 *   PATCH /api/v1/barycentre/results/{id}/status
 *     Updates the review status of a logistics center result (approve/reject/confirm).
 */
@RestController
@RequestMapping("/api/v1/barycentre")
@Tag(name = "Barycenter", description = "Optimal logistics center calculation endpoints")
public class BarycentreController {

    private final BarycentreService          barycentreService;
    private final ConsumptionSiteService     siteService;
    private final LogisticsCenterRepository  centerRepository;
    private final BarycentreMapper           mapper;

    public BarycentreController(BarycentreService barycentreService,
                                 ConsumptionSiteService siteService,
                                 LogisticsCenterRepository centerRepository,
                                 BarycentreMapper mapper) {
        this.barycentreService = barycentreService;
        this.siteService       = siteService;
        this.centerRepository  = centerRepository;
        this.mapper            = mapper;
    }

    // =========================================================================
    // POST /api/v1/barycentre/calculate
    // =========================================================================

    @PostMapping("/calculate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Calculate barycenter from stored sites",
            description = "Uses the active consumption sites already stored for the given company. " +
                          "Supports both 'weighted-barycenter' (single-step) and " +
                          "'weiszfeld-iterative' algorithms.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Calculation successful"),
            @ApiResponse(responseCode = "400", description = "Insufficient active sites or invalid request"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public BarycentreResultDto calculate(
            @Valid @RequestBody CalculateFromStoredRequest request) {

        boolean iterative = "weiszfeld-iterative".equalsIgnoreCase(request.algorithm());
        int maxIter = (request.maxIterations() != null) ? request.maxIterations() : 1_000;
        double tol  = (request.toleranceKm()  != null) ? request.toleranceKm()  : 0.01;

        LogisticsCenter center = barycentreService.calculateForCompany(
                new CalculateBarycentreUseCase.StoredSitesCommand(
                        request.companyId(), iterative, maxIter, tol));

        List<ConsumptionSite> inputSites = resolveInputSites(center);
        return mapper.toDto(center, inputSites);
    }

    // =========================================================================
    // POST /api/v1/barycentre/calculate/preview
    // =========================================================================

    @PostMapping("/calculate/preview")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Preview barycenter from inline site data",
            description = "Calculates without requiring pre-stored sites. " +
                          "The result is stored as a CANDIDATE center but input sites are not persisted. " +
                          "Useful for the 'Quick Calculate' panel in the Angular SPA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Preview calculation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or fewer than 2 sites")
    })
    public BarycentreResultDto previewCalculate(
            @Valid @RequestBody PreviewCalculateRequest request) {

        boolean iterative = "weiszfeld-iterative".equalsIgnoreCase(request.algorithm());
        int maxIter = (request.maxIterations() != null) ? request.maxIterations() : 1_000;
        double tol  = (request.toleranceKm()  != null) ? request.toleranceKm()  : 0.01;

        var cmd = new CalculateBarycentreUseCase.SimpleCalculateCommand(
                request.companyId(),
                request.sites().stream().map(PreviewCalculateRequest.InlineSite::name).toList(),
                request.sites().stream().map(PreviewCalculateRequest.InlineSite::latitude).toList(),
                request.sites().stream().map(PreviewCalculateRequest.InlineSite::longitude).toList(),
                request.sites().stream().map(PreviewCalculateRequest.InlineSite::weightTons).toList(),
                iterative);

        LogisticsCenter center = barycentreService.calculate(cmd);
        List<ConsumptionSite> inputSites = resolveInputSites(center);
        return mapper.toDto(center, inputSites);
    }

    // =========================================================================
    // GET /api/v1/barycentre/{companyId}/results
    // =========================================================================

    @GetMapping("/{companyId}/results")
    @Operation(summary = "Get all barycenter results for a company",
               description = "Returns all LogisticsCenter calculation results for the company, newest first.")
    @ApiResponse(responseCode = "200", description = "Results returned")
    public List<BarycentreResultDto> getResults(
            @Parameter(description = "Company ID", required = true)
            @PathVariable String companyId) {

        return centerRepository.findByCompanyId(companyId).stream()
                .map(center -> mapper.toDto(center, resolveInputSites(center)))
                .toList();
    }

    // =========================================================================
    // PATCH /api/v1/barycentre/results/{id}/status
    // =========================================================================

    @PatchMapping("/results/{id}/status")
    @Operation(
            summary = "Update the review status of a logistics center result",
            description = "Valid transitions: CANDIDATE -> APPROVED, CANDIDATE -> REJECTED, " +
                          "APPROVED -> CONFIRMED. A rejection reason is required when status = REJECTED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition or missing reason"),
            @ApiResponse(responseCode = "404", description = "Result not found")
    })
    public BarycentreResultDto updateResultStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {

        LogisticsCenter center = centerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Logistics center result not found: " + id));

        switch (request.status().toUpperCase()) {
            case "APPROVED"   -> center.approve(request.notes());
            case "REJECTED"   -> center.reject(
                    request.notes() != null ? request.notes() : "Rejected via API");
            case "CONFIRMED"  -> center.confirm();
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown status: " + request.status() +
                    ". Valid values: APPROVED, REJECTED, CONFIRMED");
        }

        centerRepository.save(center);
        return mapper.toDto(center, resolveInputSites(center));
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    /**
     * Resolves the ConsumptionSite objects that were used as inputs for a
     * logistics center. Falls back to an empty list if some sites were deleted
     * after the calculation was run (snapshot semantics).
     */
    private List<ConsumptionSite> resolveInputSites(LogisticsCenter center) {
        return center.getInputSiteIds().stream()
                .flatMap(siteId -> siteService.findById(siteId).stream())
                .toList();
    }

    // =========================================================================
    // Request body records
    // =========================================================================

    public record CalculateFromStoredRequest(
            @NotBlank(message = "companyId must not be blank")
            String companyId,

            @NotBlank(message = "algorithm must not be blank")
            String algorithm,     // "weighted-barycenter" | "weiszfeld-iterative"

            Integer maxIterations,
            Double  toleranceKm) {}

    public record PreviewCalculateRequest(
            String companyId,     // may be null for a stateless preview

            @NotBlank(message = "algorithm must not be blank")
            String algorithm,

            Integer maxIterations,
            Double  toleranceKm,

            List<InlineSite> sites) {

        public record InlineSite(
                String name,
                double latitude,
                double longitude,
                double weightTons) {}
    }

    public record UpdateStatusRequest(
            @NotBlank(message = "status must not be blank")
            String status,

            String notes) {}
}
