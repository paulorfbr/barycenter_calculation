package com.logistics.shared.domain.event;

import com.logistics.shared.domain.vo.GeoCoordinate;

import java.time.Instant;
import java.util.UUID;

/** Published by Site Service when a consumption site's position or weight changes. */
public record SiteUpdatedEvent(
        String        eventId,
        String        eventType,
        Instant       occurredAt,
        String        aggregateId,   // siteId
        String        companyId,
        GeoCoordinate newCoordinate,
        double        newWeightTons
) implements DomainEvent {

    // DomainEvent interface compatibility methods
    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    public static SiteUpdatedEvent of(String siteId, String companyId,
                                      GeoCoordinate coord, double tons) {
        return new SiteUpdatedEvent(
                UUID.randomUUID().toString(), "SiteUpdated",
                Instant.now(), siteId, companyId, coord, tons);
    }
}
