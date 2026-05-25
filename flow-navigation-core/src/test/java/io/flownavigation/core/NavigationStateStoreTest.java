package io.flownavigation.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NavigationStateStoreTest {

    private NavigationStateStore store;

    @BeforeEach
    void setUp() {
        store = new NavigationStateStore();
    }

    @Test
    void shouldStoreAndRetrieveValue() {
        store.put("key", "value");
        assertTrue(store.get("key", String.class).isPresent());
        assertEquals("value", store.get("key", String.class).get());
    }

    @Test
    void shouldReturnEmptyForMissingKey() {
        assertFalse(store.get("missing", String.class).isPresent());
    }

    @Test
    void shouldRemoveValue() {
        store.put("key", "value");
        store.remove("key");
        assertFalse(store.contains("key"));
    }

    @Test
    void shouldNotifyListenersOnPut() {
        List<NavigationStateStore.StateChangeEvent> events = new ArrayList<>();
        store.addListener(events::add);

        store.put("key", "value");

        assertEquals(1, events.size());
        assertEquals("key", events.get(0).key());
        assertNull(events.get(0).oldValue());
        assertEquals("value", events.get(0).newValue());
    }

    @Test
    void shouldNotifyListenersOnRemove() {
        store.put("key", "value");

        List<NavigationStateStore.StateChangeEvent> events = new ArrayList<>();
        store.addListener(events::add);

        store.remove("key");

        assertEquals(1, events.size());
        assertEquals("value", events.get(0).oldValue());
        assertNull(events.get(0).newValue());
    }

    @Test
    void shouldClearAllValues() {
        store.put("a", 1);
        store.put("b", 2);
        store.clear();
        assertFalse(store.contains("a"));
        assertFalse(store.contains("b"));
    }

    @Test
    void shouldReturnAllValues() {
        store.put("x", 10);
        store.put("y", 20);

        var all = store.getAll();
        assertEquals(2, all.size());
    }
}
