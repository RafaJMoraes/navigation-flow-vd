package io.flownavigation.dialog;

import java.util.Optional;

/**
 * Represents the result of a dialog interaction.
 */
public class DialogResult<T> {

    public enum Status {
        CONFIRMED,
        CANCELLED,
        DISMISSED
    }

    private final Status status;
    private final T value;

    private DialogResult(Status status, T value) {
        this.status = status;
        this.value = value;
    }

    public static <T> DialogResult<T> confirmed(T value) {
        return new DialogResult<>(Status.CONFIRMED, value);
    }

    public static <T> DialogResult<T> cancelled() {
        return new DialogResult<>(Status.CANCELLED, null);
    }

    public static <T> DialogResult<T> dismissed() {
        return new DialogResult<>(Status.DISMISSED, null);
    }

    public Status getStatus() {
        return status;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public boolean isConfirmed() {
        return status == Status.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public boolean isDismissed() {
        return status == Status.DISMISSED;
    }
}
