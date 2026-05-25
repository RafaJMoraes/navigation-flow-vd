package io.flownavigation.core;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the navigation stack with configurable maximum size.
 */
public class NavigationStack {

    private static final Logger log = LoggerFactory.getLogger(NavigationStack.class);
    private static final int DEFAULT_MAX_SIZE = 50;

    private final Deque<NavigationEntry> stack;
    private final int maxSize;

    public NavigationStack() {
        this(DEFAULT_MAX_SIZE);
    }

    public NavigationStack(int maxSize) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize must be at least 1");
        }
        this.maxSize = maxSize;
        this.stack = new ArrayDeque<>();
    }

    public void push(NavigationEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry must not be null");
        }
        if (stack.size() >= maxSize) {
            NavigationEntry removed = stack.removeLast();
            log.debug("Stack overflow: removed oldest entry {}", removed.getRoute());
        }
        stack.push(entry);
        log.debug("Pushed entry: {}", entry.getRoute());
    }

    public Optional<NavigationEntry> pop() {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        NavigationEntry entry = stack.pop();
        log.debug("Popped entry: {}", entry.getRoute());
        return Optional.of(entry);
    }

    public Optional<NavigationEntry> peek() {
        return Optional.ofNullable(stack.peek());
    }

    public Optional<NavigationEntry> peekPrevious() {
        if (stack.size() < 2) {
            return Optional.empty();
        }
        var iterator = stack.iterator();
        iterator.next(); // skip current
        return Optional.of(iterator.next());
    }

    public void replace(NavigationEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry must not be null");
        }
        if (!stack.isEmpty()) {
            stack.pop();
        }
        stack.push(entry);
        log.debug("Replaced current with: {}", entry.getRoute());
    }

    public void clear() {
        stack.clear();
        log.debug("Stack cleared");
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public List<NavigationEntry> getHistory() {
        return Collections.unmodifiableList(List.copyOf(stack));
    }
}
