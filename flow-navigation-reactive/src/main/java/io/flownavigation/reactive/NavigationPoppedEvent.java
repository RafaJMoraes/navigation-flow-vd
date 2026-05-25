package io.flownavigation.reactive;

import io.flownavigation.core.NavigationEntry;

import java.time.Instant;

/**
 * Event emitted when a route is popped from the navigation stack.
 */
public record NavigationPoppedEvent(
        NavigationEntry entry,
        String newCurrentRoute,
        Instant timestamp
) implements ReactiveNavigationEvent {

    public NavigationPoppedEvent(NavigationEntry entry, String newCurrentRoute) {
        this(entry, newCurrentRoute, Instant.now());
    }

    @Override
    public String route() {
        return entry.getRoute();
    }

    @Override
    public EventType eventType() {
        return EventType.POPPED;
    }
}
