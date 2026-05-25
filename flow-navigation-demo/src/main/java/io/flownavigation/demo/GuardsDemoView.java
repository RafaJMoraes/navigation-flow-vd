package io.flownavigation.demo;

import io.flownavigation.core.NavigationGuard;
import io.flownavigation.core.NavigationManager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demonstrates navigation guards and interceptors.
 */
@Route(value = "guards", layout = MainLayout.class)
@PageTitle("Guards Demo")
public class GuardsDemoView extends VerticalLayout {

    private final NavigationManager navigationManager = new NavigationManager();
    private final Span resultLabel = new Span();
    private boolean guardEnabled = false;

    public GuardsDemoView() {
        add(new H2("Navigation Guards Demo"));

        Checkbox guardToggle = new Checkbox("Enable Auth Guard (blocks /admin routes)");
        guardToggle.addValueChangeListener(e -> {
            guardEnabled = e.getValue();
            if (guardEnabled) {
                navigationManager.beforeEach((context, target) -> {
                    if (target.startsWith("/admin")) {
                        return NavigationGuard.GuardResult.DENY;
                    }
                    return NavigationGuard.GuardResult.ALLOW;
                });
            }
        });

        TextField routeField = new TextField("Route to navigate");
        routeField.setPlaceholder("/admin/settings or /public/page");

        Button navigateBtn = new Button("Navigate", e -> {
            boolean success = navigationManager.push(routeField.getValue());
            resultLabel.setText(success ? "Navigation allowed" : "Navigation BLOCKED by guard");
        });

        add(guardToggle, routeField, navigateBtn);
        add(new Paragraph("Result:"), resultLabel);
    }
}
