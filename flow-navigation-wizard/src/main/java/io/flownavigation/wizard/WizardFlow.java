package io.flownavigation.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines a wizard flow with ordered steps.
 */
public class WizardFlow {

    private final String id;
    private final String title;
    private final List<WizardStep> steps;

    private WizardFlow(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.steps = Collections.unmodifiableList(new ArrayList<>(builder.steps));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<WizardStep> getSteps() {
        return steps;
    }

    public int getStepCount() {
        return steps.size();
    }

    public WizardStep getStep(int index) {
        if (index < 0 || index >= steps.size()) {
            throw new IndexOutOfBoundsException("Step index " + index + " out of range [0, " + steps.size() + ")");
        }
        return steps.get(index);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private String title;
        private final List<WizardStep> steps = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder step(WizardStep step) {
            this.steps.add(step);
            return this;
        }

        public WizardFlow build() {
            if (steps.isEmpty()) {
                throw new IllegalStateException("WizardFlow must have at least one step");
            }
            return new WizardFlow(this);
        }
    }
}
