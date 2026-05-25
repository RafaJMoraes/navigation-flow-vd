package io.flownavigation.demo;

import io.flownavigation.breadcrumb.BreadcrumbConfig;
import io.flownavigation.breadcrumb.BreadcrumbItem;
import io.flownavigation.breadcrumb.BreadcrumbTrail;
import io.flownavigation.core.NavigationManager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demonstrates breadcrumb trail generation.
 */
@Route(value = "breadcrumb", layout = MainLayout.class)
@PageTitle("Breadcrumb Demo")
public class BreadcrumbDemoView extends VerticalLayout {

    private final NavigationManager navigationManager = new NavigationManager();
    private final BreadcrumbTrail breadcrumbTrail;
    private final HorizontalLayout breadcrumbDisplay = new HorizontalLayout();

    public BreadcrumbDemoView() {
        add(new H2("Breadcrumb Navigation Demo"));

        BreadcrumbConfig config = new BreadcrumbConfig()
                .home("/", "Home")
                .route("/students", "Students")
                .route("/students/details", "Student Details")
                .route("/students/enrollment", "Enrollment")
                .route("/settings", "Settings");

        breadcrumbTrail = new BreadcrumbTrail(navigationManager, config);

        Button navHome = new Button("Home", e -> navigate("/"));
        Button navStudents = new Button("Students", e -> navigate("/students"));
        Button navDetails = new Button("Details", e -> navigate("/students/details"));
        Button navEnrollment = new Button("Enrollment", e -> navigate("/students/enrollment"));

        HorizontalLayout navButtons = new HorizontalLayout(navHome, navStudents, navDetails, navEnrollment);

        add(navButtons, breadcrumbDisplay);
        updateBreadcrumbs();
    }

    private void navigate(String route) {
        navigationManager.push(route);
        updateBreadcrumbs();
    }

    private void updateBreadcrumbs() {
        breadcrumbDisplay.removeAll();

        List<BreadcrumbItem> trail = breadcrumbTrail.getTrailWithHome();
        for (int i = 0; i < trail.size(); i++) {
            BreadcrumbItem item = trail.get(i);
            Span label = new Span(item.label());
            if (item.active()) {
                label.getStyle().set("font-weight", "bold");
            }
            breadcrumbDisplay.add(label);
            if (i < trail.size() - 1) {
                breadcrumbDisplay.add(new Span(" > "));
            }
        }
    }
}
