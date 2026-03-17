package com.logistics.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

/**
 * Base contract for all domain events flowing over the Kafka event bus.
 *
 * Jackson polymorphic serialisation is configured here so that the shared
 * Kafka consumer/producer infrastructure can deserialise any event type
 * without service-specific configuration.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CompanyCreatedEvent.class,         name = "CompanyCreated"),
    @JsonSubTypes.Type(value = CompanyStatusChangedEvent.class,   name = "CompanyStatusChanged"),
    @JsonSubTypes.Type(value = SiteAddedEvent.class,              name = "SiteAdded"),
    @JsonSubTypes.Type(value = SiteUpdatedEvent.class,            name = "SiteUpdated"),
    @JsonSubTypes.Type(value = SiteRemovedEvent.class,            name = "SiteRemoved"),
    @JsonSubTypes.Type(value = BarycentreCalculatedEvent.class,   name = "BarycentreCalculated"),
})
public interface DomainEvent {

    /** Unique identifier for this event instance (UUID). */
    String getEventId();

    /** Canonical event type name — matches the {@code @JsonSubTypes.Type.name}. */
    String getEventType();

    /** Wall-clock time when the event was generated. */
    Instant getOccurredAt();

    /** The aggregate that produced this event (company ID, site ID, etc.). */
    String getAggregateId();
}
