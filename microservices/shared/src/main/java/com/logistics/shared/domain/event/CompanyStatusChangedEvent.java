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

    public static CompanyStatusChangedEvent of(String companyId, String prev, String next) {
        return new CompanyStatusChangedEvent(
                UUID.randomUUID().toString(), "CompanyStatusChanged",
                Instant.now(), companyId, prev, next);
    }
}
