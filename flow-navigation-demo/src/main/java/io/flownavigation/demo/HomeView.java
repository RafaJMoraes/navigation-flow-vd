package io.flownavigation.demo;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Home view for the demo application.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Home - Flow Navigation Demo")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H2("Flow Navigation Library Demo"));
        add(new Paragraph("This application demonstrates the features of the Flow Navigation library:"));
        add(new Paragraph("- Push/Pop/Replace navigation with stack management"));
        add(new Paragraph("- Wizard flows with multi-step navigation"));
        add(new Paragraph("- Dialog-based navigation with result handling"));
        add(new Paragraph("- Automatic breadcrumb trail generation"));
        add(new Paragraph("- Reactive navigation events with Project Reactor"));
        add(new Paragraph("- Navigation guards and interceptors"));
    }
}
