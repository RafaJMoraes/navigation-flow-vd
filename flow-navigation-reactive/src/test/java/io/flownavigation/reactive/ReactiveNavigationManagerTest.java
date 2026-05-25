package io.flownavigation.reactive;

import io.flownavigation.core.NavigationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ReactiveNavigationManagerTest {

    private NavigationManager navigationManager;
    private ReactiveNavigationManager reactiveManager;

    @BeforeEach
    void setUp() {
        navigationManager = new NavigationManager();
        reactiveManager = new ReactiveNavigationManager(navigationManager);
    }

    @Test
    void shouldEmitPushEvents() {
        StepVerifier.create(reactiveManager.pushEvents().take(2))
                .then(() -> {
                    navigationManager.push("/home");
                    navigationManager.push("/users");
                })
                .expectNextMatches(e -> e.entry().getRoute().equals("/home"))
                .expectNextMatches(e -> e.entry().getRoute().equals("/users"))
                .verifyComplete();
    }

    @Test
    void shouldEmitPopEvents() {
        navigationManager.push("/home");
        navigationManager.push("/users");

        StepVerifier.create(reactiveManager.popEvents().take(1))
                .then(() -> navigationManager.pop())
                .expectNextMatches(e -> e.entry().getRoute().equals("/users"))
                .verifyComplete();
    }

    @Test
    void shouldEmitReplaceEvents() {
        navigationManager.push("/old");

        StepVerifier.create(reactiveManager.replaceEvents().take(1))
                .then(() -> navigationManager.replace("/new"))
                .expectNextMatches(e -> e.entry().getRoute().equals("/new"))
                .verifyComplete();
    }

    @Test
    void shouldEmitStateChangedEvents() {
        StepVerifier.create(reactiveManager.stateChanges().take(1))
                .then(() -> navigationManager.state().put("key", "value"))
                .expectNextMatches(e -> e.key().equals("key") && e.newValue().equals("value"))
                .verifyComplete();
    }

    @Test
    void shouldEmitRouteChanges() {
        StepVerifier.create(reactiveManager.routeChanges().take(2))
                .then(() -> {
                    navigationManager.push("/first");
                    navigationManager.push("/second");
                })
                .expectNext("/first")
                .expectNext("/second")
                .verifyComplete();
    }
}
