package io.flownavigation.breadcrumb;

import io.flownavigation.core.NavigationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BreadcrumbTrailTest {

    private NavigationManager navigationManager;
    private BreadcrumbTrail trail;
    private BreadcrumbConfig config;

    @BeforeEach
    void setUp() {
        navigationManager = new NavigationManager();
        config = new BreadcrumbConfig()
                .home("/", "Home")
                .route("/students", "Students")
                .route("/students/details", "Student Details")
                .route("/settings", "Settings");
        trail = new BreadcrumbTrail(navigationManager, config);
    }

    @Test
    void shouldReturnEmptyTrailWhenNoNavigation() {
        List<BreadcrumbItem> items = trail.getTrail();
        assertTrue(items.isEmpty());
    }

    @Test
    void shouldBuildTrailFromHistory() {
        navigationManager.push("/");
        navigationManager.push("/students");
        navigationManager.push("/students/details");

        List<BreadcrumbItem> items = trail.getTrail();
        assertEquals(3, items.size());
        assertEquals("Home", items.get(0).label());
        assertEquals("Students", items.get(1).label());
        assertEquals("Student Details", items.get(2).label());
    }

    @Test
    void shouldMarkLastItemAsActive() {
        navigationManager.push("/");
        navigationManager.push("/students");

        List<BreadcrumbItem> items = trail.getTrail();
        assertFalse(items.get(0).active());
        assertTrue(items.get(1).active());
    }

    @Test
    void shouldIncludeHomeInTrailWithHome() {
        navigationManager.push("/students");

        List<BreadcrumbItem> items = trail.getTrailWithHome();
        assertEquals(2, items.size());
        assertEquals("Home", items.get(0).label());
        assertEquals("/", items.get(0).route());
    }

    @Test
    void shouldNotDuplicateHomeInTrailWithHome() {
        navigationManager.push("/");
        navigationManager.push("/students");

        List<BreadcrumbItem> items = trail.getTrailWithHome();
        long homeCount = items.stream().filter(i -> i.route().equals("/")).count();
        assertEquals(1, homeCount);
    }

    @Test
    void shouldUseDynamicLabels() {
        config.dynamicRoute("/users/:id", params -> "User " + params.getOrDefault("id", "?"));

        navigationManager.push("/users/:id");

        List<BreadcrumbItem> items = trail.getTrail();
        assertFalse(items.isEmpty());
    }

    @Test
    void shouldFallbackToDefaultLabel() {
        navigationManager.push("/unknown-route");

        List<BreadcrumbItem> items = trail.getTrail();
        assertEquals("Unknown route", items.get(0).label());
    }

    @Test
    void shouldDeduplicateRoutes() {
        navigationManager.push("/students");
        navigationManager.push("/students/details");
        navigationManager.push("/students");

        List<BreadcrumbItem> items = trail.getTrail();
        long studentCount = items.stream().filter(i -> i.route().equals("/students")).count();
        assertEquals(1, studentCount);
    }
}
