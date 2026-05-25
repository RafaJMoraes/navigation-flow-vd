package io.flownavigation.demo;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

/**
 * Main layout with navigation drawer for the demo application.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Flow Navigation Demo");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.add(
                new RouterLink("Home", HomeView.class),
                new RouterLink("Push/Pop Demo", PushPopView.class),
                new RouterLink("Wizard Demo", WizardDemoView.class),
                new RouterLink("Dialog Demo", DialogDemoView.class),
                new RouterLink("Breadcrumb Demo", BreadcrumbDemoView.class),
                new RouterLink("Reactive Demo", ReactiveDemoView.class),
                new RouterLink("Guards Demo", GuardsDemoView.class)
        );
        addToDrawer(drawer);
    }
}
