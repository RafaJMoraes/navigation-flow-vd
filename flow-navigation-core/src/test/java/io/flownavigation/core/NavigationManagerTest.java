package io.flownavigation.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NavigationManagerTest {

    private NavigationManager manager;

    @BeforeEach
    void setUp() {
        manager = new NavigationManager();
    }

    @Test
    void shouldPushRoute() {
        assertTrue(manager.push("/home"));
        assertTrue(manager.current().isPresent());
        assertEquals("/home", manager.current().get().getRoute());
    }

    @Test
    void shouldPushRouteWithParams() {
        manager.push("/users", Map.of("id", 42));
        assertEquals(42, manager.current().get().getParams().get("id"));
    }

    @Test
    void shouldPopRoute() {
        manager.push("/home");
        manager.push("/users");

        var popped = manager.pop();
        assertTrue(popped.isPresent());
        assertEquals("/users", popped.get().getRoute());
        assertEquals("/home", manager.current().get().getRoute());
    }

    @Test
    void shouldReplaceRoute() {
        manager.push("/home");
        manager.push("/old");
        assertTrue(manager.replace("/new"));
        assertEquals("/new", manager.current().get().getRoute());
    }

    @Test
    void shouldReturnPreviousRoute() {
        manager.push("/first");
        manager.push("/second");

        assertTrue(manager.previous().isPresent());
        assertEquals("/first", manager.previous().get().getRoute());
    }

    @Test
    void shouldClearStack() {
        manager.push("/a");
        manager.push("/b");
        manager.clear();
        assertFalse(manager.current().isPresent());
    }

    @Test
    void shouldBlockNavigationWithGuard() {
        manager.beforeEach((context, target) -> {
            if (target.startsWith("/admin")) {
                return NavigationGuard.GuardResult.DENY;
            }
            return NavigationGuard.GuardResult.ALLOW;
        });

        assertTrue(manager.push("/public"));
        assertFalse(manager.push("/admin/settings"));

        assertEquals("/public", manager.current().get().getRoute());
    }

    @Test
    void shouldFireNavigationEvents() {
        List<NavigationEvent> events = new ArrayList<>();
        manager.addEventListener(events::add);

        manager.push("/home");
        manager.push("/users");
        manager.pop();
        manager.clear();

        assertEquals(4, events.size());
        assertEquals(NavigationEvent.Type.PUSHED, events.get(0).getType());
        assertEquals(NavigationEvent.Type.PUSHED, events.get(1).getType());
        assertEquals(NavigationEvent.Type.POPPED, events.get(2).getType());
        assertEquals(NavigationEvent.Type.CLEARED, events.get(3).getType());
    }

    @Test
    void shouldCallInterceptors() {
        List<String> intercepted = new ArrayList<>();
        manager.addInterceptor(new NavigationInterceptor() {
            @Override
            public void beforeNavigation(String from, String to) {
                intercepted.add("before:" + from + "->" + to);
            }

            @Override
            public void afterNavigation(String from, String to) {
                intercepted.add("after:" + from + "->" + to);
            }
        });

        manager.push("/home");
        assertEquals(2, intercepted.size());
        assertEquals("before:null->/home", intercepted.get(0));
        assertEquals("after:null->/home", intercepted.get(1));
    }

    @Test
    void shouldManageState() {
        manager.state().put("selectedUser", "John");
        assertTrue(manager.state().get("selectedUser", String.class).isPresent());
        assertEquals("John", manager.state().get("selectedUser", String.class).get());
    }

    @Test
    void shouldGetNavigationContext() {
        manager.push("/home");
        manager.push("/users");

        NavigationContext context = manager.getContext();
        assertEquals("/users", context.getCurrentRoute());
        assertTrue(context.getPreviousRoute().isPresent());
        assertEquals("/home", context.getPreviousRoute().get());
    }

    @Test
    void shouldReturnHistory() {
        manager.push("/a");
        manager.push("/b");
        manager.push("/c");

        var history = manager.getHistory();
        assertEquals(3, history.size());
    }
}
