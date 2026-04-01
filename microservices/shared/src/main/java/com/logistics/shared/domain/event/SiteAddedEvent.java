package com.logistics.shared.domain.event;

import com.logistics.shared.domain.vo.GeoCoordinate;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by Site Service when a consumption site is added to a company.
 * Calculation Service listens to trigger an automatic recalculation when
 * auto-recalculate is enabled for the company.
 */
public record SiteAddedEvent(
        String        eventId,
        String        eventType,
        Instant       occurredAt,
        String        aggregateId,   // siteId
        String        companyId,
        String        siteName,
        GeoCoordinate coordinate,
        double        weightTons
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

    public static SiteAddedEvent of(String siteId, String companyId,
                                    String name, GeoCoordinate coord, double tons) {
        return new SiteAddedEvent(
                UUID.randomUUID().toString(), "SiteAdded",
                Instant.now(), siteId, companyId, name, coord, tons);
    }
}
