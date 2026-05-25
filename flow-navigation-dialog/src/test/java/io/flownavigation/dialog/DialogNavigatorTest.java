package io.flownavigation.dialog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DialogNavigatorTest {

    private DialogNavigator navigator;

    @BeforeEach
    void setUp() {
        navigator = new DialogNavigator();
    }

    @Test
    void shouldOpenDialog() {
        navigator.dialog(Object.class).open();
        assertTrue(navigator.hasOpenDialogs());
        assertEquals(1, navigator.getDialogCount());
    }

    @Test
    void shouldCloseWithResult() {
        AtomicReference<DialogResult<String>> resultRef = new AtomicReference<>();

        navigator.<String>dialog(Object.class)
                .onResult(resultRef::set)
                .open();

        navigator.closeWithResult(DialogResult.confirmed("success"));

        assertNotNull(resultRef.get());
        assertTrue(resultRef.get().isConfirmed());
        assertEquals("success", resultRef.get().getValue().get());
        assertFalse(navigator.hasOpenDialogs());
    }

    @Test
    void shouldCancelDialog() {
        AtomicReference<DialogResult<Object>> resultRef = new AtomicReference<>();

        navigator.<Object>dialog(Object.class)
                .onResult(resultRef::set)
                .open();

        navigator.cancel();

        assertNotNull(resultRef.get());
        assertTrue(resultRef.get().isCancelled());
    }

    @Test
    void shouldDismissDialog() {
        AtomicReference<DialogResult<Object>> resultRef = new AtomicReference<>();

        navigator.<Object>dialog(Object.class)
                .onResult(resultRef::set)
                .open();

        navigator.dismiss();

        assertNotNull(resultRef.get());
        assertTrue(resultRef.get().isDismissed());
    }

    @Test
    void shouldStackMultipleDialogs() {
        navigator.dialog(Object.class).open();
        navigator.dialog(Object.class).open();
        navigator.dialog(Object.class).open();

        assertEquals(3, navigator.getDialogCount());
    }

    @Test
    void shouldCloseAllDialogs() {
        navigator.dialog(Object.class).open();
        navigator.dialog(Object.class).open();

        navigator.closeAll();
        assertFalse(navigator.hasOpenDialogs());
    }

    @Test
    void shouldReportNoOpenDialogsInitially() {
        assertFalse(navigator.hasOpenDialogs());
        assertEquals(0, navigator.getDialogCount());
    }
}
