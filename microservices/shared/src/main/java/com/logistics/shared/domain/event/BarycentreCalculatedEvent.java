package com.logistics.shared.domain.event;

import com.logistics.shared.domain.vo.GeoCoordinate;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by Calculation Service after a successful barycenter run.
 * Consumed by Dashboard Service to update the logistics center KPI card
 * and map overlay without polling.
 */
public record BarycentreCalculatedEvent(
        String        eventId,
        String        eventType,
        Instant       occurredAt,
        String        aggregateId,           // logisticsCenterId
        String        companyId,
        GeoCoordinate optimalPosition,
        double        totalWeightedTons,
        int           inputSiteCount,
        int           iterationCount,
        double        convergenceErrorKm,
        String        algorithmDescription,
        String        status                 // CANDIDATE
) implements DomainEvent {

    public static BarycentreCalculatedEvent of(String centerId,
                                               String companyId,
                                               GeoCoordinate position,
                                               double tons,
                                               int siteCount,
                                               int iterations,
                                               double errorKm,
                                               String algorithm) {
        return new BarycentreCalculatedEvent(
                UUID.randomUUID().toString(), "BarycentreCalculated",
                Instant.now(), centerId, companyId, position, tons,
                siteCount, iterations, errorKm, algorithm, "CANDIDATE");
    }
}
