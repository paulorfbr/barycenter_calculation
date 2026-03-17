package com.logistics.calculation.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for a barycenter calculation.
 *
 * Two modes:
 *   1. Inline — provide {@code sites} directly.
 *   2. For a company — provide only {@code companyId}; the service fetches sites.
 *
 * Both modes are handled by the same endpoint POST /api/v1/calculations.
 * If {@code companyId} is set and {@code sites} is empty, mode 2 is used.
 */
@Schema(description = "Barycenter calculation request")
public record CalculationRequest(

        @Schema(description = "Company ID — if set without sites, active sites are fetched from Site Service",
                example = "550e8400-e29b-41d4-a716-446655440000")
        String companyId,

        @Valid
        @NotEmpty(message = "At least 2 sites are required for an inline calculation")
        @Size(min = 2, message = "At least 2 sites are required for a meaningful barycenter calculation")
        @Schema(description = "Inline site inputs — required if companyId is not set")
        List<SiteInput> sites,

        @Schema(description = "true = Weiszfeld iterative refinement; false = single-step weighted centroid",
                defaultValue = "false")
        boolean useIterative,

        @Schema(description = "Maximum iterations for Weiszfeld (ignored when useIterative=false)",
                defaultValue = "1000")
        Integer maxIterations,

        @Schema(description = "Convergence tolerance in km for Weiszfeld (ignored when useIterative=false)",
                defaultValue = "0.01")
        Double toleranceKm

) {}
