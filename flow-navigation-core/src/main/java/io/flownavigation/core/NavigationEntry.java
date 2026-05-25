package io.flownavigation.core;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single entry in the navigation stack.
 */
public class NavigationEntry {

    private final String route;
    private final Map<String, Object> params;
    private final Map<String, Object> state;
    private final Instant timestamp;
    private final String origin;

    private NavigationEntry(Builder builder) {
        this.route = Objects.requireNonNull(builder.route, "route must not be null");
        this.params = Collections.unmodifiableMap(new HashMap<>(builder.params));
        this.state = Collections.unmodifiableMap(new HashMap<>(builder.state));
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.origin = builder.origin;
    }

    public String getRoute() {
        return route;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getOrigin() {
        return origin;
    }

    public static Builder builder(String route) {
        return new Builder(route);
    }

    public static class Builder {
        private final String route;
        private Map<String, Object> params = new HashMap<>();
        private Map<String, Object> state = new HashMap<>();
        private Instant timestamp;
        private String origin;

        public Builder(String route) {
            this.route = route;
        }

        public Builder params(Map<String, Object> params) {
            this.params = params != null ? params : new HashMap<>();
            return this;
        }

        public Builder param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Builder state(Map<String, Object> state) {
            this.state = state != null ? state : new HashMap<>();
            return this;
        }

        public Builder stateEntry(String key, Object value) {
            this.state.put(key, value);
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public NavigationEntry build() {
            return new NavigationEntry(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavigationEntry that = (NavigationEntry) o;
        return Objects.equals(route, that.route) &&
                Objects.equals(params, that.params) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, params, timestamp);
    }

    @Override
    public String toString() {
        return "NavigationEntry{route='" + route + "', origin='" + origin + "', timestamp=" + timestamp + "}";
    }
}
