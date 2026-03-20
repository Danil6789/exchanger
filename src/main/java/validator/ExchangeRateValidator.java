package validator;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class ExchangeRateValidator {

    // ==================== ПУБЛИЧНЫЕ МЕТОДЫ ====================

    public ValidationResult validateRate(String rate) {
        ValidationResult result = new ValidationResult();
        validateNumber(rate, "rate", "курс", result);
        return result;
    }

    public ValidationResult validateAndParsePath(String pathCode) {
        ValidationResult result = new ValidationResult();

        if (isEmpty(pathCode) || pathCode.equals("/")) {
            result.addError("path", "Валюты пары отсутствуют в адресе");
            return result;
        }

        String pair = pathCode.substring(1);
        if (pair.length() != 6) {
            result.addError("pair", "Код пары должен быть 6 символов (USDEUR)");
            return result;
        }

        validateCurrency(pair.substring(0, 3), "baseCode", result);
        validateCurrency(pair.substring(3, 6), "targetCode", result);

        if (result.isValid()) {
            result.putData("pair", pair);
            result.putData("baseCode", pair.substring(0, 3));
            result.putData("targetCode", pair.substring(3, 6));
        }

        return result;
    }

    public ValidationResult validateCreateRequest(String baseCode, String targetCode, String rate) {
        ValidationResult result = new ValidationResult();

        validateCurrency(baseCode, "baseCurrencyCode", result);
        validateCurrency(targetCode, "targetCurrencyCode", result);
        validateNumber(rate, "rate", "курс", result);

        if (result.isValid()) {
            checkDifferentCurrencies(
                    result.getData("baseCurrencyCode"),
                    result.getData("targetCurrencyCode"),
                    result
            );
        }

        return result;
    }

    public ValidationResult validateExchangeRequest(String from, String to, String amount) {
        ValidationResult result = new ValidationResult();

        validateCurrency(from, "from", result);
        validateCurrency(to, "to", result);
        validateNumber(amount, "amount", "сумма", result);

        if (result.isValid()) {
            checkDifferentCurrencies(
                    result.getData("from"),
                    result.getData("to"),
                    result
            );
        }

        return result;
    }

    // ==================== ПРИВАТНЫЕ МЕТОДЫ ====================

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void validateCurrency(String code, String fieldName, ValidationResult result) {
        validateCurrency(code, fieldName, fieldName, result);
    }

    private void validateCurrency(String code, String errorField, String storeKey, ValidationResult result) {
        if (isEmpty(code)) {
            result.addError(errorField, "Код " + errorField + " обязателен");
            return;
        }

        String normalized = code.trim().toUpperCase();

        if (normalized.length() != 3) {
            result.addError(errorField, "Код валюты должен быть 3 символа");
        } else if (!normalized.matches("[A-Z]{3}")) {
            result.addError(errorField, "Код валюты должен содержать только латинские буквы");
        } else {
            result.putData(storeKey, normalized);
        }
    }

    private void validateNumber(String value, String fieldName, String displayName, ValidationResult result) {
        if (isEmpty(value)) {
            result.addError(fieldName, "Поле " + fieldName + " обязательно");
            return;
        }

        try {
            BigDecimal num = new BigDecimal(value.trim());
            if (num.compareTo(BigDecimal.ZERO) <= 0) {
                result.addError(fieldName, displayName + " должен быть положительным числом");
            } else {
                result.putData(fieldName, num);
            }
        } catch (NumberFormatException e) {
            result.addError(fieldName, "Неверный формат " + displayName + ". Используйте число с точкой");
        }
    }

    private void checkDifferentCurrencies(String code1, String code2, ValidationResult result) {
        if (code1 != null && code2 != null && code1.equals(code2)) {
            result.addError("currencies", "Валюты должны быть разными");
        }
    }
}