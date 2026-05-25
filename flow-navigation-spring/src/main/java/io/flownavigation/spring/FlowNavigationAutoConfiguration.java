package io.flownavigation.spring;

import io.flownavigation.core.NavigationManager;
import io.flownavigation.core.NavigationStack;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Auto-configuration for Flow Navigation with Spring Boot.
 */
@Configuration
@ConditionalOnProperty(prefix = "flow-navigation", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(FlowNavigationProperties.class)
public class FlowNavigationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Scope(value = "vaadin-session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public NavigationManager navigationManager(FlowNavigationProperties properties) {
        NavigationStack stack = new NavigationStack(properties.getMaxStackSize());
        return new NavigationManager(stack);
    }

    @Bean
    @ConditionalOnMissingBean
    public VaadinNavigationIntegration vaadinNavigationIntegration(FlowNavigationProperties properties) {
        return new VaadinNavigationIntegration(properties);
    }
}
