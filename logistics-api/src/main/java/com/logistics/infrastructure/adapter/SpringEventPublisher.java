package com.logistics.infrastructure.adapter;

import com.logistics.application.port.out.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Driven adapter: DomainEventPublisher implementation backed by Spring's
 * ApplicationEventPublisher.
 *
 * Domain events published here are dispatched synchronously on the calling thread
 * (Spring's default). Any Spring component can subscribe using @EventListener.
 *
 * This replaces the JavaFX-specific InProcessEventPublisher, which dispatched
 * events on the FX Application Thread. In the REST API context there is no UI
 * thread — events are simply dispatched to all registered Spring listeners.
 *
 * Example subscriber:
 * <pre>{@code
 *   @Component
 *   public class BarycentreEventListener {
 *       @EventListener
 *       public void onCalculated(BarycentreCalculatedEvent event) {
 *           log.info("New barycenter calculated for company {}", event.companyId());
 *       }
 *   }
 * }</pre>
 */
public class SpringEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher delegate;

    public SpringEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publish(Object event) {
        delegate.publishEvent(event);
    }
}
