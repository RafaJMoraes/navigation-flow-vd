package io.flownavigation.wizard;

import java.time.Instant;

/**
 * Events emitted during wizard navigation.
 */
public class WizardEvent {

    public enum Type {
        STARTED,
        STEP_CHANGED,
        VALIDATION_FAILED,
        COMPLETED,
        CANCELLED
    }

    private final Type type;
    private final WizardStep step;
    private final int stepIndex;
    private final Instant timestamp;

    private WizardEvent(Type type, WizardStep step, int stepIndex) {
        this.type = type;
        this.step = step;
        this.stepIndex = stepIndex;
        this.timestamp = Instant.now();
    }

    public static WizardEvent started(WizardStep step) {
        return new WizardEvent(Type.STARTED, step, 0);
    }

    public static WizardEvent stepChanged(WizardStep step, int index) {
        return new WizardEvent(Type.STEP_CHANGED, step, index);
    }

    public static WizardEvent validationFailed(WizardStep step) {
        return new WizardEvent(Type.VALIDATION_FAILED, step, -1);
    }

    public static WizardEvent completed() {
        return new WizardEvent(Type.COMPLETED, null, -1);
    }

    public static WizardEvent cancelled() {
        return new WizardEvent(Type.CANCELLED, null, -1);
    }

    public Type getType() {
        return type;
    }

    public WizardStep getStep() {
        return step;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
