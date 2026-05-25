package io.flownavigation.demo;

import io.flownavigation.dialog.DialogNavigator;
import io.flownavigation.dialog.DialogResult;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demonstrates dialog navigation features.
 */
@Route(value = "dialog", layout = MainLayout.class)
@PageTitle("Dialog Demo")
public class DialogDemoView extends VerticalLayout {

    private final DialogNavigator dialogNavigator = new DialogNavigator();
    private final Span resultLabel = new Span("No dialog result yet");

    public DialogDemoView() {
        add(new H2("Dialog Navigation Demo"));

        Button openDialogBtn = new Button("Open Dialog", e -> openSampleDialog());
        Button chainDialogBtn = new Button("Chain Dialogs", e -> openChainedDialogs());

        add(openDialogBtn, chainDialogBtn);
        add(new Paragraph("Last Result:"), resultLabel);
    }

    private void openSampleDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Sample Dialog");
        dialog.add(new Paragraph("This is a navigated dialog."));

        Button confirmBtn = new Button("Confirm", e -> {
            dialogNavigator.closeWithResult(DialogResult.confirmed("User confirmed"));
            resultLabel.setText("Confirmed");
            dialog.close();
        });

        Button cancelBtn = new Button("Cancel", e -> {
            dialogNavigator.cancel();
            resultLabel.setText("Cancelled");
            dialog.close();
        });

        dialog.getFooter().add(cancelBtn, confirmBtn);

        dialogNavigator.dialog(Dialog.class).open();
        dialog.open();
    }

    private void openChainedDialogs() {
        dialogNavigator.<String>dialog(Dialog.class)
                .onResult(result -> {
                    if (result.isConfirmed()) {
                        resultLabel.setText("Chain completed: " + result.getValue().orElse(""));
                    }
                })
                .open();

        resultLabel.setText("Dialog chain started...");
    }
}
