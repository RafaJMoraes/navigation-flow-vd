package io.flownavigation.dialog;

import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a dialog entry in the dialog stack.
 */
public class DialogNavigationEntry<T> {

    private final Class<?> dialogClass;
    private final Map<String, Object> params;
    private final Instant timestamp;
    private final Consumer<DialogResult<T>> resultHandler;
    private final boolean modal;

    private DialogNavigationEntry(Builder<T> builder) {
        this.dialogClass = builder.dialogClass;
        this.params = builder.params != null ? Map.copyOf(builder.params) : Map.of();
        this.timestamp = Instant.now();
        this.resultHandler = builder.resultHandler;
        this.modal = builder.modal;
    }

    public Class<?> getDialogClass() {
        return dialogClass;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Consumer<DialogResult<T>> getResultHandler() {
        return resultHandler;
    }

    public boolean isModal() {
        return modal;
    }

    public static <T> Builder<T> builder(Class<?> dialogClass) {
        return new Builder<>(dialogClass);
    }

    public static class Builder<T> {
        private final Class<?> dialogClass;
        private Map<String, Object> params;
        private Consumer<DialogResult<T>> resultHandler;
        private boolean modal = true;

        public Builder(Class<?> dialogClass) {
            this.dialogClass = dialogClass;
        }

        public Builder<T> params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder<T> onResult(Consumer<DialogResult<T>> resultHandler) {
            this.resultHandler = resultHandler;
            return this;
        }

        public Builder<T> modal(boolean modal) {
            this.modal = modal;
            return this;
        }

        public DialogNavigationEntry<T> build() {
            return new DialogNavigationEntry<>(this);
        }
    }
}
