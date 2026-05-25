package io.flownavigation.core;

/**
 * Guard that can intercept navigation before or after it occurs.
 */
@FunctionalInterface
public interface NavigationGuard {

    /**
     * Called to evaluate if navigation should proceed.
     *
     * @param context the current navigation context
     * @param target  the target route
     * @return result indicating whether navigation should proceed
     */
    GuardResult check(NavigationContext context, String target);

    enum GuardResult {
        ALLOW,
        DENY,
        REDIRECT
    }
}
