package io.flownavigation.reactive;

import java.time.Instant;

/**
 * Base interface for all reactive navigation events.
 */
public interface ReactiveNavigationEvent {

    enum EventType {
        PUSHED,
        POPPED,
        REPLACED,
        STATE_CHANGED
    }

    String route();

    EventType eventType();

    Instant timestamp();
}
