package io.flownavigation.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the current navigation state context.
 */
public class NavigationContext {

    private final String currentRoute;
    private final String previousRoute;
    private final Map<String, Object> state;
    private final Map<String, Object> metadata;

    public NavigationContext(String currentRoute, String previousRoute,
                            Map<String, Object> state, Map<String, Object> metadata) {
        this.currentRoute = currentRoute;
        this.previousRoute = previousRoute;
        this.state = state != null ? Collections.unmodifiableMap(new HashMap<>(state)) : Collections.emptyMap();
        this.metadata = metadata != null ? Collections.unmodifiableMap(new HashMap<>(metadata)) : Collections.emptyMap();
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public Optional<String> getPreviousRoute() {
        return Optional.ofNullable(previousRoute);
    }

    public Map<String, Object> getState() {
        return state;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getStateValue(String key, Class<T> type) {
        Object value = state.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "NavigationContext{currentRoute='" + currentRoute +
                "', previousRoute='" + previousRoute + "'}";
    }
}
