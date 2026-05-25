package io.flownavigation.wizard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Context for a wizard flow, holding shared data across steps.
 */
public class WizardContext {

    private final Map<String, Object> data;
    private final Map<String, Map<String, Object>> stepData;

    public WizardContext() {
        this.data = new HashMap<>();
        this.stepData = new HashMap<>();
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public void putStepData(String stepId, String key, Object value) {
        stepData.computeIfAbsent(stepId, k -> new HashMap<>()).put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getStepData(String stepId, String key, Class<T> type) {
        Map<String, Object> step = stepData.get(stepId);
        if (step != null) {
            Object value = step.get(key);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
        }
        return Optional.empty();
    }

    public Map<String, Object> getAllData() {
        return Collections.unmodifiableMap(data);
    }

    public Map<String, Object> getStepData(String stepId) {
        Map<String, Object> step = stepData.get(stepId);
        return step != null ? Collections.unmodifiableMap(step) : Collections.emptyMap();
    }

    public void clear() {
        data.clear();
        stepData.clear();
    }
}
