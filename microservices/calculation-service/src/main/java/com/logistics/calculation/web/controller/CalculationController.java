package com.logistics.calculation.web.controller;

import com.logistics.calculation.application.service.CalculationApplicationService;
import com.logistics.calculation.web.dto.CalculationRequest;
import com.logistics.calculation.web.dto.CalculationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for barycenter calculation operations.
 *
 * POST /api/v1/calculations          — inline or stored-sites calculation
 * POST /api/v1/calculations/company/{id} — company-scoped stored-sites calculation
 * GET  /api/v1/calculations/{id}     — retrieve a past result
 */
@RestController
@RequestMapping("/api/v1/calculations")
@Tag(name = "Calculations", description = "Barycenter logistics center optimization calculations")
public class CalculationController {

    private final CalculationApplicationService service;

    public CalculationController(CalculationApplicationService service) {
        this.service = service;
    }

    // -----------------------------------------------------------------------
    // POST /api/v1/calculations
    // -----------------------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary     = "Run a barycenter calculation",
        description = """
            Executes a barycenter calculation using either:
            - Inline mode: sites and weights provided in the request body (min. 2 sites).
            - Company mode: set companyId with an empty sites list; the service fetches
              active sites from Site Service automatically.

            Algorithm options:
            - weighted-barycenter (default): single-step weighted centroid, O(n), exact.
            - weiszfeld-iterative: iterative geometric median, minimises total weighted
              transport distance — more accurate for logistics cost optimization.
            """
    )
    public CalculationResponse calculate(@Valid @RequestBody CalculationRequest request) {
        // If companyId is set but no inline sites, use the stored-sites path
        if (request.companyId() != null && !request.companyId().isBlank()
                && (request.sites() == null || request.sites().isEmpty())) {
            return service.calculateForCompany(request.companyId(), request.useIterative());
        }
        return service.calculateInline(request);
    }

    // -----------------------------------------------------------------------
    // POST /api/v1/calculations/company/{companyId}
    // -----------------------------------------------------------------------

    @PostMapping("/company/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary     = "Calculate for a company using its stored sites",
        description = "Fetches all active consumption sites for the given company from Site Service and runs the calculation."
    )
    public CalculationResponse calculateForCompany(
            @Parameter(description = "Company UUID", required = true)
            @PathVariable String companyId,

            @Parameter(description = "Use Weiszfeld iterative algorithm", example = "true")
            @RequestParam(defaultValue = "false") boolean useIterative) {

        return service.calculateForCompany(companyId, useIterative);
    }
}
