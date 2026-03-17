package com.logistics.calculation.web.dto;

import com.logistics.calculation.domain.model.LogisticsCenter;
import com.logistics.shared.domain.vo.GeoCoordinate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Response body for a completed barycenter calculation.
 */
@Schema(description = "Barycenter calculation result")
public record CalculationResponse(

        @Schema(description = "Unique identifier of the resulting logistics center (UUID)")
        String logisticsCenterId,

        @Schema(description = "Owning company ID (or 'PREVIEW' for ad-hoc calculations)")
        String companyId,

        @Schema(description = "Calculated optimal latitude in decimal degrees")
        double optimalLatitude,

        @Schema(description = "Calculated optimal longitude in decimal degrees")
        double optimalLongitude,

        @Schema(description = "Human-readable coordinate string", example = "38.6532, -95.7892")
        String formattedCoordinate,

        @Schema(description = "Algorithm used", allowableValues = {"weighted-barycenter", "weiszfeld-iterative"})
        String algorithmDescription,

        @Schema(description = "Number of iterations performed (1 for single-step algorithm)")
        int iterationCount,

        @Schema(description = "Convergence error in km at the final iteration (0 for single-step)")
        double convergenceErrorKm,

        @Schema(description = "Sum of all input site weights in tons")
        double totalWeightedTons,

        @Schema(description = "Number of input sites used")
        int inputSiteCount,

        @Schema(description = "ISO-8601 timestamp when the calculation was performed")
        Instant calculatedAt,

        @Schema(description = "Lifecycle status of this logistics center result")
        String status,

        @Schema(description = "Input sites with their distances to the optimal position")
        List<SiteInput> inputSites

) {
    /**
     * Maps a persisted {@link LogisticsCenter} and the original site inputs to
     * a response DTO enriched with per-site distances to the optimal position.
     */
    public static CalculationResponse from(LogisticsCenter center, List<SiteInput> originalSites) {
        GeoCoordinate optimal = center.getOptimalPosition();

        List<SiteInput> enrichedSites = originalSites.stream()
                .map(s -> new SiteInput(
                        s.siteId(), s.siteName(),
                        s.latitude(), s.longitude(), s.weightTons(),
                        GeoCoordinate.of(s.latitude(), s.longitude()).distanceKmTo(optimal)))
                .toList();

        return new CalculationResponse(
                center.getId(),
                center.getCompanyId(),
                optimal.getLatitude(),
                optimal.getLongitude(),
                optimal.toDisplayString(),
                center.getAlgorithmDescription(),
                center.getIterationCount(),
                center.getConvergenceErrorKm(),
                center.getTotalWeightedTons(),
                originalSites.size(),
                center.getCalculatedAt(),
                center.getStatus().name(),
                enrichedSites);
    }
}
