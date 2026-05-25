package io.flownavigation.breadcrumb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Configuration for breadcrumb route-to-label mappings and custom resolvers.
 */
public class BreadcrumbConfig {

    private final Map<String, String> routeLabels;
    private final Map<String, Function<Map<String, Object>, String>> dynamicLabels;
    private String homeRoute = "/";
    private String homeLabel = "Home";

    public BreadcrumbConfig() {
        this.routeLabels = new HashMap<>();
        this.dynamicLabels = new HashMap<>();
    }

    public BreadcrumbConfig route(String route, String label) {
        routeLabels.put(route, label);
        return this;
    }

    public BreadcrumbConfig dynamicRoute(String route, Function<Map<String, Object>, String> labelResolver) {
        dynamicLabels.put(route, labelResolver);
        return this;
    }

    public BreadcrumbConfig home(String route, String label) {
        this.homeRoute = route;
        this.homeLabel = label;
        return this;
    }

    public Optional<String> getLabel(String route) {
        return Optional.ofNullable(routeLabels.get(route));
    }

    public Optional<String> getDynamicLabel(String route, Map<String, Object> params) {
        Function<Map<String, Object>, String> resolver = dynamicLabels.get(route);
        if (resolver != null) {
            return Optional.ofNullable(resolver.apply(params));
        }
        return Optional.empty();
    }

    public String getHomeRoute() {
        return homeRoute;
    }

    public String getHomeLabel() {
        return homeLabel;
    }

    public Map<String, String> getRouteLabels() {
        return Collections.unmodifiableMap(routeLabels);
    }
}
