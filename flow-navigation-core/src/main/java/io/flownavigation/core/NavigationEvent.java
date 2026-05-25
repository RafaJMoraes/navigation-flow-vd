package io.flownavigation.core;

import java.time.Instant;

/**
 * Represents a navigation event that occurred in the system.
 */
public class NavigationEvent {

    public enum Type {
        PUSHED,
        POPPED,
        REPLACED,
        CLEARED
    }

    private final Type type;
    private final NavigationEntry entry;
    private final String previousRoute;
    private final Instant timestamp;

    private NavigationEvent(Type type, NavigationEntry entry, String previousRoute) {
        this.type = type;
        this.entry = entry;
        this.previousRoute = previousRoute;
        this.timestamp = Instant.now();
    }

    public static NavigationEvent pushed(NavigationEntry entry, String previousRoute) {
        return new NavigationEvent(Type.PUSHED, entry, previousRoute);
    }

    public static NavigationEvent popped(NavigationEntry entry, String newCurrentRoute) {
        return new NavigationEvent(Type.POPPED, entry, newCurrentRoute);
    }

    public static NavigationEvent replaced(NavigationEntry entry, String previousRoute) {
        return new NavigationEvent(Type.REPLACED, entry, previousRoute);
    }

    public static NavigationEvent cleared() {
        return new NavigationEvent(Type.CLEARED, null, null);
    }

    public Type getType() {
        return type;
    }

    public NavigationEntry getEntry() {
        return entry;
    }

    public String getPreviousRoute() {
        return previousRoute;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "NavigationEvent{type=" + type +
                ", entry=" + (entry != null ? entry.getRoute() : "null") +
                ", previousRoute='" + previousRoute + "'}";
    }
}
