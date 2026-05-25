package io.flownavigation.breadcrumb;

import java.util.Map;

/**
 * Represents a single breadcrumb item in the trail.
 */
public record BreadcrumbItem(
        String route,
        String label,
        Map<String, Object> params,
        boolean active
) {

    public BreadcrumbItem(String route, String label) {
        this(route, label, Map.of(), false);
    }

    public BreadcrumbItem(String route, String label, boolean active) {
        this(route, label, Map.of(), active);
    }

    public BreadcrumbItem withActive(boolean active) {
        return new BreadcrumbItem(route, label, params, active);
    }
}
