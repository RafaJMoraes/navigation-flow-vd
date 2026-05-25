package io.flownavigation.wizard;

import java.util.function.Predicate;

/**
 * Represents a single step in a wizard flow.
 */
public class WizardStep {

    private final String id;
    private final String title;
    private final String route;
    private final boolean optional;
    private Predicate<WizardContext> validator;

    private WizardStep(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.route = builder.route;
        this.optional = builder.optional;
        this.validator = builder.validator;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRoute() {
        return route;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean validate(WizardContext context) {
        if (validator == null) {
            return true;
        }
        return validator.test(context);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private String title;
        private String route;
        private boolean optional = false;
        private Predicate<WizardContext> validator;

        public Builder(String id) {
            this.id = id;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder route(String route) {
            this.route = route;
            return this;
        }

        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder validator(Predicate<WizardContext> validator) {
            this.validator = validator;
            return this;
        }

        public WizardStep build() {
            return new WizardStep(this);
        }
    }

    @Override
    public String toString() {
        return "WizardStep{id='" + id + "', title='" + title + "'}";
    }
}
