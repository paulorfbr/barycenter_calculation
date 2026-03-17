package com.logistics.infrastructure.adapter;

import com.logistics.application.port.out.DomainEventPublisher;
import com.logistics.domain.event.BarycentreCalculatedEvent;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-process domain event bus implementation of {@link DomainEventPublisher}.
 *
 * Supports typed listener registration so that UI components can subscribe to
 * specific event types without casting. All listeners are dispatched on the
 * JavaFX Application Thread via {@link Platform#runLater(Runnable)} to ensure
 * that subscribers can safely update observable properties and scene-graph nodes.
 *
 * Usage in a screen:
 * <pre>{@code
 *   InProcessEventPublisher bus = ServiceLocator.getInstance().getEventPublisher();
 *   bus.subscribe(BarycentreCalculatedEvent.class, event -> {
 *       kpiBarycentreCard.withValue(event.optimalPosition().toDisplayString());
 *   });
 * }</pre>
 *
 * Thread safety: the subscription lists use {@link CopyOnWriteArrayList} so that
 * background threads can publish while the FX thread iterates subscribers.
 */
public class InProcessEventPublisher implements DomainEventPublisher {

    // -------------------------------------------------------------------------
    // Typed subscriber registries
    // -------------------------------------------------------------------------

    private final List<Consumer<BarycentreCalculatedEvent>> barycentreListeners
            = new CopyOnWriteArrayList<>();

    // -------------------------------------------------------------------------
    // DomainEventPublisher contract
    // -------------------------------------------------------------------------

    @Override
    public void publish(Object event) {
        if (event instanceof BarycentreCalculatedEvent e) {
            dispatchOnFxThread(barycentreListeners, e);
        }
        // Additional event type dispatch arms are added here as the domain grows
    }

    // -------------------------------------------------------------------------
    // Typed subscription API
    // -------------------------------------------------------------------------

    /**
     * Registers a listener for {@link BarycentreCalculatedEvent}.
     * The listener is always called on the JavaFX Application Thread.
     *
     * @param listener the callback
     */
    public void subscribe(Consumer<BarycentreCalculatedEvent> listener) {
        barycentreListeners.add(listener);
    }

    /**
     * Removes a previously registered barycenter listener.
     *
     * @param listener the listener to remove
     */
    public void unsubscribe(Consumer<BarycentreCalculatedEvent> listener) {
        barycentreListeners.remove(listener);
    }

    // -------------------------------------------------------------------------
    // Internal dispatch
    // -------------------------------------------------------------------------

    private <T> void dispatchOnFxThread(List<Consumer<T>> listeners, T event) {
        if (listeners.isEmpty()) return;
        if (Platform.isFxApplicationThread()) {
            listeners.forEach(l -> l.accept(event));
        } else {
            Platform.runLater(() -> listeners.forEach(l -> l.accept(event)));
        }
    }
}
