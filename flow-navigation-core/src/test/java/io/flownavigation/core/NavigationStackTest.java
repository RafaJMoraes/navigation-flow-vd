package io.flownavigation.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NavigationStackTest {

    private NavigationStack stack;

    @BeforeEach
    void setUp() {
        stack = new NavigationStack(5);
    }

    @Test
    void shouldPushEntry() {
        NavigationEntry entry = NavigationEntry.builder("/home").build();
        stack.push(entry);

        assertEquals(1, stack.size());
        assertTrue(stack.peek().isPresent());
        assertEquals("/home", stack.peek().get().getRoute());
    }

    @Test
    void shouldPopEntry() {
        stack.push(NavigationEntry.builder("/home").build());
        stack.push(NavigationEntry.builder("/users").build());

        Optional<NavigationEntry> popped = stack.pop();
        assertTrue(popped.isPresent());
        assertEquals("/users", popped.get().getRoute());
        assertEquals(1, stack.size());
    }

    @Test
    void shouldReturnEmptyOnPopFromEmptyStack() {
        Optional<NavigationEntry> popped = stack.pop();
        assertFalse(popped.isPresent());
    }

    @Test
    void shouldReplaceCurrentEntry() {
        stack.push(NavigationEntry.builder("/home").build());
        stack.push(NavigationEntry.builder("/old").build());

        stack.replace(NavigationEntry.builder("/new").build());

        assertEquals(2, stack.size());
        assertEquals("/new", stack.peek().get().getRoute());
    }

    @Test
    void shouldRespectMaxSize() {
        NavigationStack smallStack = new NavigationStack(3);
        smallStack.push(NavigationEntry.builder("/a").build());
        smallStack.push(NavigationEntry.builder("/b").build());
        smallStack.push(NavigationEntry.builder("/c").build());
        smallStack.push(NavigationEntry.builder("/d").build());

        assertEquals(3, smallStack.size());
        assertEquals("/d", smallStack.peek().get().getRoute());
    }

    @Test
    void shouldPeekPreviousEntry() {
        stack.push(NavigationEntry.builder("/first").build());
        stack.push(NavigationEntry.builder("/second").build());

        Optional<NavigationEntry> previous = stack.peekPrevious();
        assertTrue(previous.isPresent());
        assertEquals("/first", previous.get().getRoute());
    }

    @Test
    void shouldClearStack() {
        stack.push(NavigationEntry.builder("/a").build());
        stack.push(NavigationEntry.builder("/b").build());

        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void shouldReturnHistory() {
        stack.push(NavigationEntry.builder("/a").build());
        stack.push(NavigationEntry.builder("/b").build());
        stack.push(NavigationEntry.builder("/c").build());

        var history = stack.getHistory();
        assertEquals(3, history.size());
        assertEquals("/c", history.get(0).getRoute());
        assertEquals("/a", history.get(2).getRoute());
    }

    @Test
    void shouldRejectNullEntry() {
        assertThrows(IllegalArgumentException.class, () -> stack.push(null));
    }

    @Test
    void shouldRejectInvalidMaxSize() {
        assertThrows(IllegalArgumentException.class, () -> new NavigationStack(0));
    }
}
