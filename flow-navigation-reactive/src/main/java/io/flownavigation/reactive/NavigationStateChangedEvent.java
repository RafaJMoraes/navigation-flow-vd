package io.flownavigation.reactive;

import java.time.Instant;

/**
 * Event emitted when the navigation state changes.
 */
public record NavigationStateChangedEvent(
        String key,
        Object oldValue,
        Object newValue,
        Instant timestamp
) implements ReactiveNavigationEvent {

    public NavigationStateChangedEvent(String key, Object oldValue, Object newValue) {
        this(key, oldValue, newValue, Instant.now());
    }

    @Override
    public String route() {
        return null;
    }

    @Override
    public EventType eventType() {
        return EventType.STATE_CHANGED;
    }
}
