package com.logistics.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Published when a company is activated, deactivated, or deleted. */
public record CompanyStatusChangedEvent(
        String  eventId,
        String  eventType,
        Instant occurredAt,
        String  aggregateId,
        String  previousStatus,
        String  newStatus
) implements DomainEvent {

    public static CompanyStatusChangedEvent of(String companyId, String prev, String next) {
        return new CompanyStatusChangedEvent(
                UUID.randomUUID().toString(), "CompanyStatusChanged",
                Instant.now(), companyId, prev, next);
    }
}
