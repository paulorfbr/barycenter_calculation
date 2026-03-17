package com.logistics.application.port.out;

/**
 * Outbound port: contract for publishing domain events.
 *
 * The desktop implementation uses JavaFX event bus mechanics
 * (or a simple in-process observer list). A future Spring Boot
 * implementation would delegate to ApplicationEventPublisher.
 *
 * Keeping this as a port means the application services can fire events
 * without knowing whether they travel in-process or over a message broker.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event to all registered subscribers.
     *
     * @param event any domain event record; type-safe dispatch is the
     *              responsibility of the concrete implementation
     */
    void publish(Object event);
}
