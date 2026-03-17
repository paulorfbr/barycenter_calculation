package com.logistics.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by Company Service when a new company is successfully persisted.
 * Consumed by Dashboard Service to refresh the company KPI card.
 */
public record CompanyCreatedEvent(
        String  eventId,
        String  eventType,
        Instant occurredAt,
        String  aggregateId,   // companyId
        String  companyName,
        String  companyType,   // SHIPPER | CARRIER | BOTH
        String  status         // ACTIVE | PENDING | INACTIVE
) implements DomainEvent {

    public static CompanyCreatedEvent of(String companyId, String name, String type, String status) {
        return new CompanyCreatedEvent(
                UUID.randomUUID().toString(),
                "CompanyCreated",
                Instant.now(),
                companyId,
                name,
                type,
                status
        );
    }
}
