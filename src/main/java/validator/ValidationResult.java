package validator;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private boolean valid;
    private Map<String, String> errors;
    private Map<String, Object> data;  // ← для хранения извлеченных данных

    public ValidationResult() {
        this.valid = true;
        this.errors = new HashMap<>();
        this.data = new HashMap<>();
    }

    public void addError(String field, String message) {
        this.valid = false;
        this.errors.put(field, message);
    }

    public void addError(String message) {
        this.valid = false;
        this.errors.put("general", message);
    }

    public void putData(String key, Object value) {
        this.data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    public boolean isValid() {
        return valid;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.values().iterator().next();
    }

    public void addErrors(Map<String, String> otherErrors) {
        if (otherErrors != null && !otherErrors.isEmpty()) {
            this.valid = false;
            this.errors.putAll(otherErrors);
        }
    }
}