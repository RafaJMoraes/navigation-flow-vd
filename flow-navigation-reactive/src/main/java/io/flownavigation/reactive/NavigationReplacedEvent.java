package io.flownavigation.reactive;

import io.flownavigation.core.NavigationEntry;

import java.time.Instant;

/**
 * Event emitted when the current route is replaced.
 */
public record NavigationReplacedEvent(
        NavigationEntry entry,
        String previousRoute,
        Instant timestamp
) implements ReactiveNavigationEvent {

    public NavigationReplacedEvent(NavigationEntry entry, String previousRoute) {
        this(entry, previousRoute, Instant.now());
    }

    @Override
    public String route() {
        return entry.getRoute();
    }

    @Override
    public EventType eventType() {
        return EventType.REPLACED;
    }
}
