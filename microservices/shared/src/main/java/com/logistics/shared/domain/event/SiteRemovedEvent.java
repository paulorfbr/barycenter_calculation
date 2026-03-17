package com.logistics.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Published by Site Service when a consumption site is deleted. */
public record SiteRemovedEvent(
        String  eventId,
        String  eventType,
        Instant occurredAt,
        String  aggregateId,  // siteId
        String  companyId
) implements DomainEvent {

    public static SiteRemovedEvent of(String siteId, String companyId) {
        return new SiteRemovedEvent(
                UUID.randomUUID().toString(), "SiteRemoved",
                Instant.now(), siteId, companyId);
    }
}
