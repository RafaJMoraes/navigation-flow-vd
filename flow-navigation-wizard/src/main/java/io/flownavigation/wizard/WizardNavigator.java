package io.flownavigation.wizard;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Navigator that manages progression through wizard steps.
 */
public class WizardNavigator {

    private static final Logger log = LoggerFactory.getLogger(WizardNavigator.class);

    public enum WizardStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    private final WizardFlow flow;
    private final WizardContext context;
    private int currentStepIndex = -1;
    private WizardStatus status = WizardStatus.NOT_STARTED;
    private final List<Consumer<WizardEvent>> listeners = new CopyOnWriteArrayList<>();

    public WizardNavigator(WizardFlow flow) {
        this(flow, new WizardContext());
    }

    public WizardNavigator(WizardFlow flow, WizardContext context) {
        this.flow = flow;
        this.context = context;
    }

    /**
     * Start the wizard flow at the first step.
     */
    public WizardStep start() {
        currentStepIndex = 0;
        status = WizardStatus.IN_PROGRESS;
        WizardStep step = flow.getStep(0);
        fireEvent(WizardEvent.started(step));
        log.info("Wizard '{}' started at step '{}'", flow.getId(), step.getId());
        return step;
    }

    /**
     * Move to the next step if current step is valid.
     */
    public Optional<WizardStep> next() {
        if (status != WizardStatus.IN_PROGRESS) {
            return Optional.empty();
        }

        WizardStep currentStep = getCurrentStep();
        if (!currentStep.validate(context)) {
            log.debug("Validation failed for step '{}'", currentStep.getId());
            fireEvent(WizardEvent.validationFailed(currentStep));
            return Optional.empty();
        }

        if (currentStepIndex >= flow.getStepCount() - 1) {
            return Optional.empty();
        }

        currentStepIndex++;
        WizardStep nextStep = flow.getStep(currentStepIndex);
        fireEvent(WizardEvent.stepChanged(nextStep, currentStepIndex));
        log.debug("Advanced to step '{}' ({}/{})", nextStep.getId(), currentStepIndex + 1, flow.getStepCount());
        return Optional.of(nextStep);
    }

    /**
     * Move to the previous step.
     */
    public Optional<WizardStep> previous() {
        if (status != WizardStatus.IN_PROGRESS || currentStepIndex <= 0) {
            return Optional.empty();
        }

        currentStepIndex--;
        WizardStep prevStep = flow.getStep(currentStepIndex);
        fireEvent(WizardEvent.stepChanged(prevStep, currentStepIndex));
        log.debug("Went back to step '{}' ({}/{})", prevStep.getId(), currentStepIndex + 1, flow.getStepCount());
        return Optional.of(prevStep);
    }

    /**
     * Finish the wizard if all required steps are valid.
     */
    public boolean finish() {
        if (status != WizardStatus.IN_PROGRESS) {
            return false;
        }

        WizardStep currentStep = getCurrentStep();
        if (!currentStep.validate(context)) {
            log.debug("Cannot finish: validation failed for step '{}'", currentStep.getId());
            fireEvent(WizardEvent.validationFailed(currentStep));
            return false;
        }

        status = WizardStatus.COMPLETED;
        fireEvent(WizardEvent.completed());
        log.info("Wizard '{}' completed", flow.getId());
        return true;
    }

    /**
     * Cancel the wizard.
     */
    public void cancel() {
        status = WizardStatus.CANCELLED;
        fireEvent(WizardEvent.cancelled());
        log.info("Wizard '{}' cancelled", flow.getId());
    }

    public WizardStep getCurrentStep() {
        if (currentStepIndex < 0) {
            throw new IllegalStateException("Wizard has not been started");
        }
        return flow.getStep(currentStepIndex);
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public WizardStatus getStatus() {
        return status;
    }

    public WizardContext getContext() {
        return context;
    }

    public WizardFlow getFlow() {
        return flow;
    }

    public double getProgress() {
        if (currentStepIndex < 0) return 0.0;
        return (double) (currentStepIndex + 1) / flow.getStepCount();
    }

    public boolean isFirstStep() {
        return currentStepIndex == 0;
    }

    public boolean isLastStep() {
        return currentStepIndex == flow.getStepCount() - 1;
    }

    public boolean hasNext() {
        return status == WizardStatus.IN_PROGRESS && currentStepIndex < flow.getStepCount() - 1;
    }

    public boolean hasPrevious() {
        return status == WizardStatus.IN_PROGRESS && currentStepIndex > 0;
    }

    public void addEventListener(Consumer<WizardEvent> listener) {
        listeners.add(listener);
    }

    public void removeEventListener(Consumer<WizardEvent> listener) {
        listeners.remove(listener);
    }

    private void fireEvent(WizardEvent event) {
        for (Consumer<WizardEvent> listener : listeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                log.error("Error in wizard event listener", e);
            }
        }
    }
}
