package io.flownavigation.core;

/**
 * Interceptor for navigation events that can modify or observe navigation state.
 */
public interface NavigationInterceptor {

    /**
     * Called before navigation occurs.
     *
     * @param from the origin route (may be null for initial navigation)
     * @param to   the target route
     */
    default void beforeNavigation(String from, String to) {}

    /**
     * Called after navigation has completed.
     *
     * @param from the origin route (may be null for initial navigation)
     * @param to   the target route
     */
    default void afterNavigation(String from, String to) {}
}
