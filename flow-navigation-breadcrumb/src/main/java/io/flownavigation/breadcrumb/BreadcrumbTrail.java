package io.flownavigation.breadcrumb;

import io.flownavigation.core.NavigationEntry;
import io.flownavigation.core.NavigationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates and manages breadcrumb trails from navigation history.
 */
public class BreadcrumbTrail {

    private static final Logger log = LoggerFactory.getLogger(BreadcrumbTrail.class);

    private final NavigationManager navigationManager;
    private final BreadcrumbConfig config;

    public BreadcrumbTrail(NavigationManager navigationManager, BreadcrumbConfig config) {
        this.navigationManager = navigationManager;
        this.config = config;
    }

    /**
     * Get the current breadcrumb trail based on navigation history.
     */
    public List<BreadcrumbItem> getTrail() {
        List<NavigationEntry> history = navigationManager.getHistory();
        if (history.isEmpty()) {
            return Collections.emptyList();
        }

        List<BreadcrumbItem> trail = new ArrayList<>();
        List<String> seen = new ArrayList<>();

        // Process history in reverse (oldest first)
        List<NavigationEntry> ordered = new ArrayList<>(history);
        Collections.reverse(ordered);

        for (int i = 0; i < ordered.size(); i++) {
            NavigationEntry entry = ordered.get(i);
            String route = entry.getRoute();

            if (seen.contains(route)) {
                continue;
            }
            seen.add(route);

            String label = resolveLabel(route, entry.getParams());
            boolean active = (i == ordered.size() - 1);
            trail.add(new BreadcrumbItem(route, label, entry.getParams(), active));
        }

        return Collections.unmodifiableList(trail);
    }

    /**
     * Get breadcrumb trail with home as the first item.
     */
    public List<BreadcrumbItem> getTrailWithHome() {
        List<BreadcrumbItem> trail = new ArrayList<>();
        trail.add(new BreadcrumbItem(config.getHomeRoute(), config.getHomeLabel(), false));

        List<BreadcrumbItem> current = getTrail();
        for (BreadcrumbItem item : current) {
            if (!item.route().equals(config.getHomeRoute())) {
                trail.add(item);
            }
        }

        return Collections.unmodifiableList(trail);
    }

    /**
     * Navigate to a specific breadcrumb item (pop back to that route).
     */
    public boolean navigateTo(BreadcrumbItem item) {
        Optional<NavigationEntry> current = navigationManager.current();
        if (current.isEmpty() || current.get().getRoute().equals(item.route())) {
            return false;
        }

        // Pop until we reach the target route
        while (navigationManager.current().isPresent() &&
                !navigationManager.current().get().getRoute().equals(item.route())) {
            Optional<NavigationEntry> popped = navigationManager.pop();
            if (popped.isEmpty()) {
                break;
            }
        }

        return navigationManager.current()
                .map(e -> e.getRoute().equals(item.route()))
                .orElse(false);
    }

    private String resolveLabel(String route, Map<String, Object> params) {
        // Try dynamic label first
        Optional<String> dynamic = config.getDynamicLabel(route, params);
        if (dynamic.isPresent()) {
            return dynamic.get();
        }

        // Try static label
        Optional<String> staticLabel = config.getLabel(route);
        if (staticLabel.isPresent()) {
            return staticLabel.get();
        }

        // Fallback: capitalize last segment of route
        return defaultLabel(route);
    }

    private String defaultLabel(String route) {
        if (route == null || route.isBlank() || route.equals("/")) {
            return "Home";
        }
        String[] segments = route.split("/");
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].isBlank()) {
                String last = segments[i];
                return last.substring(0, 1).toUpperCase() + last.substring(1).replace("-", " ");
            }
        }
        return "Home";
    }
}
