package com.logistics.domain.event;

import com.logistics.domain.model.LogisticsCenter;
import com.logistics.domain.vo.GeoCoordinate;

import java.time.Instant;

/**
 * Domain event published when the barycenter engine produces a new
 * optimal logistics center position.
 *
 * Consumers of this event in the application layer:
 *   - DashboardService: refreshes the barycenter KPI card and map overlay.
 *   - LocationService: optionally persists the result as a new LOGISTICS_CENTER location.
 *   - NotificationService: triggers a "Calculation complete" toast in the UI.
 *
 * Using a domain event (rather than a direct service call) decouples the
 * calculation engine from consumers and makes it straightforward to add
 * reactive subscribers as the system grows.
 */
public record BarycentreCalculatedEvent(
        String          eventId,
        Instant         occurredAt,
        String          companyId,
        String          logisticsCenterId,
        GeoCoordinate   optimalPosition,
        double          totalWeightedTons,
        int             inputSiteCount,
        int             iterationCount,
        double          convergenceErrorKm,
        String          algorithmDescription) {

    /** Convenience factory used by the calculation engine. */
    public static BarycentreCalculatedEvent from(LogisticsCenter center) {
        return new BarycentreCalculatedEvent(
                java.util.UUID.randomUUID().toString(),
                Instant.now(),
                center.getCompanyId(),
                center.getId(),
                center.getOptimalPosition(),
                center.getTotalWeightedVolume().tons(),
                center.getInputSiteIds().size(),
                center.getIterationCount(),
                center.getConvergenceErrorKm(),
                center.getAlgorithmDescription()
        );
    }
}
