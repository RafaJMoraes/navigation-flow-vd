package io.flownavigation.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Flow Navigation.
 */
@ConfigurationProperties(prefix = "flow-navigation")
public class FlowNavigationProperties {

    private boolean enabled = true;
    private int maxStackSize = 50;
    private boolean restoreNavigation = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public boolean isRestoreNavigation() {
        return restoreNavigation;
    }

    public void setRestoreNavigation(boolean restoreNavigation) {
        this.restoreNavigation = restoreNavigation;
    }
}
