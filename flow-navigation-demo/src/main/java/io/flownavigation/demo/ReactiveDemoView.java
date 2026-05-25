package io.flownavigation.demo;

import io.flownavigation.core.NavigationManager;
import io.flownavigation.reactive.ReactiveNavigationManager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demonstrates reactive navigation events.
 */
@Route(value = "reactive", layout = MainLayout.class)
@PageTitle("Reactive Demo")
public class ReactiveDemoView extends VerticalLayout {

    private final NavigationManager navigationManager = new NavigationManager();
    private final ReactiveNavigationManager reactiveManager;
    private final VerticalLayout eventLog = new VerticalLayout();

    public ReactiveDemoView() {
        add(new H2("Reactive Navigation Demo"));

        reactiveManager = new ReactiveNavigationManager(navigationManager);

        // Subscribe to all events
        reactiveManager.events().subscribe(event ->
                getUI().ifPresent(ui -> ui.access(() ->
                        eventLog.addComponentAsFirst(new Span(
                                "[" + event.eventType() + "] " + event.route()))
                ))
        );

        TextField routeField = new TextField("Route");
        Button pushBtn = new Button("Push", e -> {
            if (!routeField.getValue().isBlank()) {
                navigationManager.push(routeField.getValue());
            }
        });
        Button popBtn = new Button("Pop", e -> navigationManager.pop());

        add(routeField, pushBtn, popBtn);
        add(new Paragraph("Event Log:"));
        add(eventLog);
    }
}
