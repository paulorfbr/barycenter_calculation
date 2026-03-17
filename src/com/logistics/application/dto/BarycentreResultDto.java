package com.logistics.application.dto;

import java.util.List;

/**
 * Data Transfer Object for a barycenter calculation result.
 *
 * Consumed by:
 *   - BarycentreResultScreen (JavaFX) via the view-model mapper.
 *   - POST /api/v1/barycentre response body (React integration).
 *
 * Immutability and record semantics make this trivially serialisable with
 * Jackson or Gson without any annotations.
 */
public record BarycentreResultDto(

        String logisticsCenterId,
        String companyId,

        // Primary output
        double optimalLatitude,
        double optimalLongitude,
        String formattedCoordinate,

        // Algorithm metadata
        String algorithmDescription,
        int    iterationCount,
        double convergenceErrorKm,

        // Weight summary
        double totalWeightedTons,
        int    inputSiteCount,

        // Input sites snapshot (for display in the result panel)
        List<InputSiteDto> inputSites,

        // Status
        String status         // CANDIDATE / APPROVED / REJECTED / CONFIRMED

) {
    /**
     * A single input consumption site as reflected in the result DTO.
     */
    public record InputSiteDto(
            String siteId,
            String siteName,
            double latitude,
            double longitude,
            double weightTons,
            double distanceToOptimalKm) {}
}
