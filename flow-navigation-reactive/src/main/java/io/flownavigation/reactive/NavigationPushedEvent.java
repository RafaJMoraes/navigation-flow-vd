package io.flownavigation.reactive;

import io.flownavigation.core.NavigationEntry;

import java.time.Instant;

/**
 * Event emitted when a new route is pushed onto the navigation stack.
 */
public record NavigationPushedEvent(
        NavigationEntry entry,
        String previousRoute,
        Instant timestamp
) implements ReactiveNavigationEvent {

    public NavigationPushedEvent(NavigationEntry entry, String previousRoute) {
        this(entry, previousRoute, Instant.now());
    }

    @Override
    public String route() {
        return entry.getRoute();
    }

    @Override
    public EventType eventType() {
        return EventType.PUSHED;
    }
}
