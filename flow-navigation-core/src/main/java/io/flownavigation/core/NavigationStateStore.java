package io.flownavigation.core;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Shared state store for navigation state management.
 */
public class NavigationStateStore {

    private final Map<String, Object> state = new ConcurrentHashMap<>();
    private final List<Consumer<StateChangeEvent>> listeners = new CopyOnWriteArrayList<>();

    public void put(String key, Object value) {
        Object old = state.put(key, value);
        notifyListeners(new StateChangeEvent(key, old, value));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = state.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(state.get(key));
    }

    public Object remove(String key) {
        Object old = state.remove(key);
        if (old != null) {
            notifyListeners(new StateChangeEvent(key, old, null));
        }
        return old;
    }

    public boolean contains(String key) {
        return state.containsKey(key);
    }

    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(state);
    }

    public void clear() {
        state.clear();
    }

    public void addListener(Consumer<StateChangeEvent> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<StateChangeEvent> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(StateChangeEvent event) {
        for (Consumer<StateChangeEvent> listener : listeners) {
            listener.accept(event);
        }
    }

    public record StateChangeEvent(String key, Object oldValue, Object newValue) {}
}
