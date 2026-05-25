package io.flownavigation.demo;

import io.flownavigation.core.NavigationEntry;
import io.flownavigation.core.NavigationManager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Map;

/**
 * Demonstrates push, pop, and replace navigation operations.
 */
@Route(value = "push-pop", layout = MainLayout.class)
@PageTitle("Push/Pop Demo")
public class PushPopView extends VerticalLayout {

    private final NavigationManager navigationManager = new NavigationManager();
    private final Span currentRouteLabel = new Span();
    private final Span previousRouteLabel = new Span();
    private final Span stackSizeLabel = new Span();

    public PushPopView() {
        add(new H2("Push/Pop Navigation Demo"));

        TextField routeField = new TextField("Route");
        routeField.setPlaceholder("/example/route");

        Button pushButton = new Button("Push", e -> {
            String route = routeField.getValue();
            if (!route.isBlank()) {
                navigationManager.push(route);
                updateDisplay();
            }
        });

        Button popButton = new Button("Pop", e -> {
            navigationManager.pop();
            updateDisplay();
        });

        Button replaceButton = new Button("Replace", e -> {
            String route = routeField.getValue();
            if (!route.isBlank()) {
                navigationManager.replace(route);
                updateDisplay();
            }
        });

        Button clearButton = new Button("Clear", e -> {
            navigationManager.clear();
            updateDisplay();
        });

        HorizontalLayout buttons = new HorizontalLayout(pushButton, popButton, replaceButton, clearButton);

        add(routeField, buttons);
        add(new Paragraph("Current Route:"), currentRouteLabel);
        add(new Paragraph("Previous Route:"), previousRouteLabel);
        add(new Paragraph("Stack Size:"), stackSizeLabel);

        updateDisplay();
    }

    private void updateDisplay() {
        currentRouteLabel.setText(
                navigationManager.current().map(NavigationEntry::getRoute).orElse("(empty)"));
        previousRouteLabel.setText(
                navigationManager.previous().map(NavigationEntry::getRoute).orElse("(empty)"));
        stackSizeLabel.setText(String.valueOf(navigationManager.getStack().size()));
    }
}
