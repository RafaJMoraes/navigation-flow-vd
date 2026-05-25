package io.flownavigation.reactive;

import io.flownavigation.core.NavigationEntry;
import io.flownavigation.core.NavigationEvent;
import io.flownavigation.core.NavigationManager;
import io.flownavigation.core.NavigationStateStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Reactive wrapper around NavigationManager that provides a Flux of navigation events.
 */
public class ReactiveNavigationManager {

    private static final Logger log = LoggerFactory.getLogger(ReactiveNavigationManager.class);

    private final NavigationManager navigationManager;
    private final Sinks.Many<ReactiveNavigationEvent> eventSink;

    public ReactiveNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();

        setupEventBridge();
    }

    private void setupEventBridge() {
        navigationManager.addEventListener(this::bridgeEvent);

        navigationManager.state().addListener(change -> {
            NavigationStateChangedEvent event = new NavigationStateChangedEvent(
                    change.key(), change.oldValue(), change.newValue());
            eventSink.tryEmitNext(event);
        });
    }

    private void bridgeEvent(NavigationEvent event) {
        ReactiveNavigationEvent reactiveEvent = switch (event.getType()) {
            case PUSHED -> new NavigationPushedEvent(event.getEntry(), event.getPreviousRoute());
            case POPPED -> new NavigationPoppedEvent(event.getEntry(), event.getPreviousRoute());
            case REPLACED -> new NavigationReplacedEvent(event.getEntry(), event.getPreviousRoute());
            case CLEARED -> null;
        };

        if (reactiveEvent != null) {
            eventSink.tryEmitNext(reactiveEvent);
        }
    }

    /**
     * Returns a Flux of all navigation events.
     */
    public Flux<ReactiveNavigationEvent> events() {
        return eventSink.asFlux();
    }

    /**
     * Returns a Flux filtered to only pushed events.
     */
    public Flux<NavigationPushedEvent> pushEvents() {
        return events()
                .filter(NavigationPushedEvent.class::isInstance)
                .cast(NavigationPushedEvent.class);
    }

    /**
     * Returns a Flux filtered to only popped events.
     */
    public Flux<NavigationPoppedEvent> popEvents() {
        return events()
                .filter(NavigationPoppedEvent.class::isInstance)
                .cast(NavigationPoppedEvent.class);
    }

    /**
     * Returns a Flux filtered to only replaced events.
     */
    public Flux<NavigationReplacedEvent> replaceEvents() {
        return events()
                .filter(NavigationReplacedEvent.class::isInstance)
                .cast(NavigationReplacedEvent.class);
    }

    /**
     * Returns a Flux filtered to only state change events.
     */
    public Flux<NavigationStateChangedEvent> stateChanges() {
        return events()
                .filter(NavigationStateChangedEvent.class::isInstance)
                .cast(NavigationStateChangedEvent.class);
    }

    /**
     * Returns a Flux of route changes (emits the new route string on each navigation).
     */
    public Flux<String> routeChanges() {
        return events()
                .filter(e -> e.route() != null)
                .map(ReactiveNavigationEvent::route);
    }

    /**
     * Get the underlying NavigationManager.
     */
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    /**
     * Complete the event stream (for cleanup).
     */
    public void dispose() {
        eventSink.tryEmitComplete();
    }
}
