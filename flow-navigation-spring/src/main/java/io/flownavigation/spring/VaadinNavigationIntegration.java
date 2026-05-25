package io.flownavigation.spring;

import io.flownavigation.core.NavigationManager;
import io.flownavigation.core.NavigationStack;

import com.vaadin.flow.server.VaadinSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration between Flow Navigation and Vaadin session management.
 */
public class VaadinNavigationIntegration {

    private static final Logger log = LoggerFactory.getLogger(VaadinNavigationIntegration.class);
    private static final String NAV_MANAGER_KEY = "flow-navigation-manager";

    private final FlowNavigationProperties properties;

    public VaadinNavigationIntegration(FlowNavigationProperties properties) {
        this.properties = properties;
    }

    /**
     * Get or create a NavigationManager scoped to the current VaadinSession.
     */
    public NavigationManager getNavigationManager() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            log.warn("No VaadinSession available, creating unscoped NavigationManager");
            return new NavigationManager(new NavigationStack(properties.getMaxStackSize()));
        }

        NavigationManager manager = (NavigationManager) session.getAttribute(NAV_MANAGER_KEY);
        if (manager == null) {
            manager = new NavigationManager(new NavigationStack(properties.getMaxStackSize()));
            session.setAttribute(NAV_MANAGER_KEY, manager);
            log.debug("Created new NavigationManager for session {}", session.getSession().getId());
        }
        return manager;
    }

    /**
     * Store navigation state in VaadinSession for persistence across page refreshes.
     */
    public void persistState(VaadinSession session, NavigationManager manager) {
        if (session != null && properties.isRestoreNavigation()) {
            session.setAttribute(NAV_MANAGER_KEY, manager);
        }
    }

    /**
     * Restore navigation state from VaadinSession.
     */
    public NavigationManager restoreState(VaadinSession session) {
        if (session != null && properties.isRestoreNavigation()) {
            NavigationManager manager = (NavigationManager) session.getAttribute(NAV_MANAGER_KEY);
            if (manager != null) {
                log.debug("Restored NavigationManager from session");
                return manager;
            }
        }
        return new NavigationManager(new NavigationStack(properties.getMaxStackSize()));
    }
}
