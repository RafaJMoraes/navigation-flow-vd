package io.flownavigation.demo;

import io.flownavigation.wizard.WizardContext;
import io.flownavigation.wizard.WizardFlow;
import io.flownavigation.wizard.WizardNavigator;
import io.flownavigation.wizard.WizardStep;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demonstrates wizard flow navigation.
 */
@Route(value = "wizard", layout = MainLayout.class)
@PageTitle("Wizard Demo")
public class WizardDemoView extends VerticalLayout {

    private final WizardNavigator navigator;
    private final ProgressBar progressBar = new ProgressBar(0, 1);
    private final Span stepLabel = new Span();
    private final Span statusLabel = new Span();

    public WizardDemoView() {
        add(new H2("Wizard Flow Demo"));

        WizardFlow flow = WizardFlow.builder("student-enrollment")
                .title("Student Enrollment")
                .step(WizardStep.builder("personal-info").title("Personal Information").route("/wizard/personal").build())
                .step(WizardStep.builder("contact").title("Contact Details").route("/wizard/contact").build())
                .step(WizardStep.builder("documents").title("Documents").route("/wizard/documents").build())
                .step(WizardStep.builder("review").title("Review & Confirm").route("/wizard/review").build())
                .build();

        navigator = new WizardNavigator(flow);

        Button startButton = new Button("Start Wizard", e -> {
            navigator.start();
            updateDisplay();
        });

        Button nextButton = new Button("Next", e -> {
            navigator.next();
            updateDisplay();
        });

        Button prevButton = new Button("Previous", e -> {
            navigator.previous();
            updateDisplay();
        });

        Button finishButton = new Button("Finish", e -> {
            navigator.finish();
            updateDisplay();
        });

        Button cancelButton = new Button("Cancel", e -> {
            navigator.cancel();
            updateDisplay();
        });

        HorizontalLayout buttons = new HorizontalLayout(startButton, prevButton, nextButton, finishButton, cancelButton);

        add(progressBar, stepLabel, statusLabel, buttons);
        updateDisplay();
    }

    private void updateDisplay() {
        progressBar.setValue(navigator.getProgress());
        statusLabel.setText("Status: " + navigator.getStatus());

        if (navigator.getStatus() == WizardNavigator.WizardStatus.IN_PROGRESS) {
            WizardStep step = navigator.getCurrentStep();
            stepLabel.setText("Step " + (navigator.getCurrentStepIndex() + 1) +
                    "/" + navigator.getFlow().getStepCount() + ": " + step.getTitle());
        } else {
            stepLabel.setText("");
        }
    }
}
