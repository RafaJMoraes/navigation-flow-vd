package io.flownavigation.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central navigation manager responsible for managing the navigation stack,
 * guards, interceptors, and state.
 */
public class NavigationManager {

    private static final Logger log = LoggerFactory.getLogger(NavigationManager.class);

    private final NavigationStack stack;
    private final NavigationStateStore stateStore;
    private final List<NavigationGuard> beforeGuards;
    private final List<NavigationInterceptor> interceptors;
    private final List<Consumer<NavigationEvent>> eventListeners;

    public NavigationManager() {
        this(new NavigationStack());
    }

    public NavigationManager(NavigationStack stack) {
        this.stack = stack;
        this.stateStore = new NavigationStateStore();
        this.beforeGuards = new CopyOnWriteArrayList<>();
        this.interceptors = new CopyOnWriteArrayList<>();
        this.eventListeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Push a new route onto the navigation stack.
     */
    public boolean push(String route) {
        return push(route, Map.of(), Map.of());
    }

    /**
     * Push a new route with parameters onto the navigation stack.
     */
    public boolean push(String route, Map<String, Object> params) {
        return push(route, params, Map.of());
    }

    /**
     * Push a new route with parameters and state onto the navigation stack.
     */
    public boolean push(String route, Map<String, Object> params, Map<String, Object> state) {
        String currentRoute = current().map(NavigationEntry::getRoute).orElse(null);

        if (!checkGuards(currentRoute, route)) {
            log.debug("Navigation to '{}' blocked by guard", route);
            return false;
        }

        interceptors.forEach(i -> i.beforeNavigation(currentRoute, route));

        NavigationEntry entry = NavigationEntry.builder(route)
                .params(params)
                .state(state)
                .origin(currentRoute)
                .build();

        stack.push(entry);

        interceptors.forEach(i -> i.afterNavigation(currentRoute, route));
        fireEvent(NavigationEvent.pushed(entry, currentRoute));

        log.info("Navigated to: {}", route);
        return true;
    }

    /**
     * Pop the current route from the stack and return to the previous one.
     */
    public Optional<NavigationEntry> pop() {
        Optional<NavigationEntry> popped = stack.pop();
        popped.ifPresent(entry -> {
            String newCurrent = current().map(NavigationEntry::getRoute).orElse(null);
            interceptors.forEach(i -> i.afterNavigation(entry.getRoute(), newCurrent));
            fireEvent(NavigationEvent.popped(entry, newCurrent));
            log.info("Popped from: {} to: {}", entry.getRoute(), newCurrent);
        });
        return popped;
    }

    /**
     * Pop the current route from the stack and push a new route.
     * The current route is removed from the history before navigating to the new route,
     * similar to popAndPushNamed in Flutter or replace-style navigation in React Router.
     */
    public boolean popAndPush(String route) {
        return popAndPush(route, Map.of(), Map.of());
    }

    /**
     * Pop the current route and push a new route with parameters.
     */
    public boolean popAndPush(String route, Map<String, Object> params) {
        return popAndPush(route, params, Map.of());
    }

    /**
     * Pop the current route and push a new route with parameters and state.
     */
    public boolean popAndPush(String route, Map<String, Object> params, Map<String, Object> state) {
        String currentRoute = current().map(NavigationEntry::getRoute).orElse(null);

        if (!checkGuards(currentRoute, route)) {
            log.debug("Pop and push to '{}' blocked by guard", route);
            return false;
        }

        interceptors.forEach(i -> i.beforeNavigation(currentRoute, route));

        Optional<NavigationEntry> popped = stack.pop();
        popped.ifPresent(entry -> {
            String afterPopRoute = current().map(NavigationEntry::getRoute).orElse(null);
            fireEvent(NavigationEvent.popped(entry, afterPopRoute));
        });

        NavigationEntry entry = NavigationEntry.builder(route)
                .params(params)
                .state(state)
                .origin(currentRoute)
                .build();

        stack.push(entry);

        interceptors.forEach(i -> i.afterNavigation(currentRoute, route));
        fireEvent(NavigationEvent.pushed(entry, currentRoute));

        log.info("Pop and pushed from: {} to: {}", currentRoute, route);
        return true;
    }

    /**
     * Replace the current route without adding to history.
     */
    public boolean replace(String route, Map<String, Object> params, Map<String, Object> state) {
        String currentRoute = current().map(NavigationEntry::getRoute).orElse(null);

        if (!checkGuards(currentRoute, route)) {
            log.debug("Replace to '{}' blocked by guard", route);
            return false;
        }

        interceptors.forEach(i -> i.beforeNavigation(currentRoute, route));

        NavigationEntry entry = NavigationEntry.builder(route)
                .params(params)
                .state(state)
                .origin(currentRoute)
                .build();

        stack.replace(entry);

        interceptors.forEach(i -> i.afterNavigation(currentRoute, route));
        fireEvent(NavigationEvent.replaced(entry, currentRoute));

        log.info("Replaced with: {}", route);
        return true;
    }

    /**
     * Replace the current route.
     */
    public boolean replace(String route) {
        return replace(route, Map.of(), Map.of());
    }

    /**
     * Get the current navigation entry.
     */
    public Optional<NavigationEntry> current() {
        return stack.peek();
    }

    /**
     * Get the previous navigation entry.
     */
    public Optional<NavigationEntry> previous() {
        return stack.peekPrevious();
    }

    /**
     * Clear the entire navigation stack.
     */
    public void clear() {
        stack.clear();
        fireEvent(NavigationEvent.cleared());
        log.info("Navigation stack cleared");
    }

    /**
     * Get the navigation context for the current state.
     */
    public NavigationContext getContext() {
        String currentRoute = current().map(NavigationEntry::getRoute).orElse(null);
        String previousRoute = previous().map(NavigationEntry::getRoute).orElse(null);
        return new NavigationContext(currentRoute, previousRoute, stateStore.getAll(), Map.of());
    }

    /**
     * Get the shared state store.
     */
    public NavigationStateStore state() {
        return stateStore;
    }

    /**
     * Get the navigation stack.
     */
    public NavigationStack getStack() {
        return stack;
    }

    /**
     * Add a before-navigation guard.
     */
    public void beforeEach(NavigationGuard guard) {
        beforeGuards.add(guard);
    }

    /**
     * Remove a guard.
     */
    public void removeGuard(NavigationGuard guard) {
        beforeGuards.remove(guard);
    }

    /**
     * Add a navigation interceptor.
     */
    public void addInterceptor(NavigationInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Remove an interceptor.
     */
    public void removeInterceptor(NavigationInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    /**
     * Add a navigation event listener.
     */
    public void addEventListener(Consumer<NavigationEvent> listener) {
        eventListeners.add(listener);
    }

    /**
     * Remove an event listener.
     */
    public void removeEventListener(Consumer<NavigationEvent> listener) {
        eventListeners.remove(listener);
    }

    /**
     * Get the navigation history.
     */
    public List<NavigationEntry> getHistory() {
        return stack.getHistory();
    }

    private boolean checkGuards(String from, String to) {
        NavigationContext context = new NavigationContext(from, null, stateStore.getAll(), Map.of());
        for (NavigationGuard guard : beforeGuards) {
            NavigationGuard.GuardResult result = guard.check(context, to);
            if (result != NavigationGuard.GuardResult.ALLOW) {
                return false;
            }
        }
        return true;
    }

    private void fireEvent(NavigationEvent event) {
        for (Consumer<NavigationEvent> listener : eventListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                log.error("Error in navigation event listener", e);
            }
        }
    }
}
