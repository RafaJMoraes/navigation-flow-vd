package io.flownavigation.dialog;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages dialog navigation with stack, chaining, and result callbacks.
 */
public class DialogNavigator {

    private static final Logger log = LoggerFactory.getLogger(DialogNavigator.class);

    private final Deque<DialogNavigationEntry<?>> dialogStack = new ArrayDeque<>();

    /**
     * Open a dialog and return a builder for configuration.
     */
    public <T> DialogBuilder<T> dialog(Class<?> dialogClass) {
        return new DialogBuilder<>(this, dialogClass);
    }

    /**
     * Push a dialog onto the dialog stack.
     */
    <T> void pushDialog(DialogNavigationEntry<T> entry) {
        dialogStack.push(entry);
        log.debug("Opened dialog: {}", entry.getDialogClass().getSimpleName());
    }

    /**
     * Close the current dialog with a result.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void closeWithResult(DialogResult<T> result) {
        DialogNavigationEntry<?> entry = dialogStack.poll();
        if (entry != null) {
            Consumer handler = entry.getResultHandler();
            if (handler != null) {
                handler.accept(result);
            }
            log.debug("Closed dialog: {} with status: {}",
                    entry.getDialogClass().getSimpleName(), result.getStatus());
        }
    }

    /**
     * Close the current dialog as cancelled.
     */
    public void cancel() {
        closeWithResult(DialogResult.cancelled());
    }

    /**
     * Close the current dialog as dismissed.
     */
    public void dismiss() {
        closeWithResult(DialogResult.dismissed());
    }

    /**
     * Get the current active dialog entry.
     */
    public Optional<DialogNavigationEntry<?>> current() {
        return Optional.ofNullable(dialogStack.peek());
    }

    /**
     * Check if any dialogs are open.
     */
    public boolean hasOpenDialogs() {
        return !dialogStack.isEmpty();
    }

    /**
     * Get the number of open dialogs.
     */
    public int getDialogCount() {
        return dialogStack.size();
    }

    /**
     * Close all open dialogs.
     */
    public void closeAll() {
        while (!dialogStack.isEmpty()) {
            dismiss();
        }
    }

    /**
     * Fluent builder for dialog navigation.
     */
    public static class DialogBuilder<T> {
        private final DialogNavigator navigator;
        private final Class<?> dialogClass;
        private Map<String, Object> params;
        private Consumer<DialogResult<T>> resultHandler;
        private boolean modal = true;
        private Class<?> thenNavigateTarget;

        DialogBuilder(DialogNavigator navigator, Class<?> dialogClass) {
            this.navigator = navigator;
            this.dialogClass = dialogClass;
        }

        public DialogBuilder<T> withParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public DialogBuilder<T> modal(boolean modal) {
            this.modal = modal;
            return this;
        }

        public DialogBuilder<T> onResult(Consumer<DialogResult<T>> handler) {
            this.resultHandler = handler;
            return this;
        }

        public DialogBuilder<T> thenNavigate(Class<?> viewClass) {
            this.thenNavigateTarget = viewClass;
            return this;
        }

        public void open() {
            DialogNavigationEntry<T> entry = DialogNavigationEntry.<T>builder(dialogClass)
                    .params(params)
                    .onResult(resultHandler)
                    .modal(modal)
                    .build();
            navigator.pushDialog(entry);
        }

        public Class<?> getThenNavigateTarget() {
            return thenNavigateTarget;
        }
    }
}
