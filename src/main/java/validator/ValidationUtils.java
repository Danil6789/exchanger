package validator;

public class ValidationUtils {

    public static String normalizeCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }

    public static String normalizeAndValidate(String value, String fieldName, ValidationResult result) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(fieldName + " не может быть пустым");
            return null;
        }
        return value.trim();
    }

    public static boolean isValidCurrencyCode(String code) {
        return code != null && code.matches("[A-Z]{3}");
    }

    public static boolean isValidSign(String sign) {
        return sign != null && sign.length() == 1 && !sign.trim().isEmpty();
    }
    public static String validateAndNormalizeCode(String code, ValidationResult result) {
        String normalized = normalizeAndValidate(code, "Код валюты", result);
        if (normalized == null) {
            return null;
        }

        if (normalized.length() != 3) {
            result.addError("Код валюты должен содержать ровно 3 символа");
            return null;
        }

        if (!isValidCurrencyCode(normalized)) {
            result.addError("Код валюты должен содержать только латинские буквы");
            return null;
        }

        return normalized;
    }
}