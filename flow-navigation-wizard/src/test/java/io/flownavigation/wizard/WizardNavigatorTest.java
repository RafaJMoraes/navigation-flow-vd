package io.flownavigation.wizard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WizardNavigatorTest {

    private WizardFlow flow;
    private WizardNavigator navigator;

    @BeforeEach
    void setUp() {
        flow = WizardFlow.builder("test-wizard")
                .title("Test Wizard")
                .step(WizardStep.builder("step1").title("Step 1").route("/step1").build())
                .step(WizardStep.builder("step2").title("Step 2").route("/step2").build())
                .step(WizardStep.builder("step3").title("Step 3").route("/step3").build())
                .build();
        navigator = new WizardNavigator(flow);
    }

    @Test
    void shouldStartAtFirstStep() {
        WizardStep step = navigator.start();
        assertEquals("step1", step.getId());
        assertEquals(WizardNavigator.WizardStatus.IN_PROGRESS, navigator.getStatus());
    }

    @Test
    void shouldAdvanceToNextStep() {
        navigator.start();
        Optional<WizardStep> next = navigator.next();
        assertTrue(next.isPresent());
        assertEquals("step2", next.get().getId());
    }

    @Test
    void shouldGoToPreviousStep() {
        navigator.start();
        navigator.next();
        Optional<WizardStep> prev = navigator.previous();
        assertTrue(prev.isPresent());
        assertEquals("step1", prev.get().getId());
    }

    @Test
    void shouldNotGoPreviousOnFirstStep() {
        navigator.start();
        Optional<WizardStep> prev = navigator.previous();
        assertFalse(prev.isPresent());
    }

    @Test
    void shouldNotAdvancePastLastStep() {
        navigator.start();
        navigator.next();
        navigator.next();
        Optional<WizardStep> next = navigator.next();
        assertFalse(next.isPresent());
    }

    @Test
    void shouldFinishWizard() {
        navigator.start();
        navigator.next();
        navigator.next();
        assertTrue(navigator.finish());
        assertEquals(WizardNavigator.WizardStatus.COMPLETED, navigator.getStatus());
    }

    @Test
    void shouldCancelWizard() {
        navigator.start();
        navigator.cancel();
        assertEquals(WizardNavigator.WizardStatus.CANCELLED, navigator.getStatus());
    }

    @Test
    void shouldTrackProgress() {
        navigator.start();
        assertEquals(1.0 / 3, navigator.getProgress(), 0.01);
        navigator.next();
        assertEquals(2.0 / 3, navigator.getProgress(), 0.01);
        navigator.next();
        assertEquals(1.0, navigator.getProgress(), 0.01);
    }

    @Test
    void shouldKnowFirstAndLastStep() {
        navigator.start();
        assertTrue(navigator.isFirstStep());
        assertFalse(navigator.isLastStep());

        navigator.next();
        navigator.next();
        assertFalse(navigator.isFirstStep());
        assertTrue(navigator.isLastStep());
    }

    @Test
    void shouldValidateStepBeforeAdvancing() {
        WizardFlow validatedFlow = WizardFlow.builder("validated")
                .title("Validated")
                .step(WizardStep.builder("s1").title("S1")
                        .validator(ctx -> ctx.get("name", String.class).isPresent())
                        .build())
                .step(WizardStep.builder("s2").title("S2").build())
                .build();

        WizardNavigator nav = new WizardNavigator(validatedFlow);
        nav.start();

        // Should not advance without data
        assertFalse(nav.next().isPresent());

        // Add data and try again
        nav.getContext().put("name", "Test");
        assertTrue(nav.next().isPresent());
    }

    @Test
    void shouldFireEvents() {
        List<WizardEvent> events = new ArrayList<>();
        navigator.addEventListener(events::add);

        navigator.start();
        navigator.next();
        navigator.finish();

        assertEquals(3, events.size());
        assertEquals(WizardEvent.Type.STARTED, events.get(0).getType());
        assertEquals(WizardEvent.Type.STEP_CHANGED, events.get(1).getType());
        assertEquals(WizardEvent.Type.COMPLETED, events.get(2).getType());
    }

    @Test
    void shouldProvideContext() {
        navigator.start();
        WizardContext context = navigator.getContext();
        context.put("key", "value");
        assertEquals("value", context.get("key", String.class).get());
    }
}
