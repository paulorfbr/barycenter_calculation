package com.logistics.company.application.port.out;

import com.logistics.shared.domain.event.DomainEvent;

/**
 * Outbound driven port for publishing domain events to the message bus.
 *
 * The Kafka adapter in the infrastructure layer implements this interface.
 * The application service is not coupled to Kafka, enabling in-process
 * event delivery during unit tests.
 */
public interface CompanyEventPublisher {

    /**
     * Publishes a domain event asynchronously to the configured Kafka topic.
     *
     * @param event the event to publish
     */
    void publish(DomainEvent event);
}
