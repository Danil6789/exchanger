package validator;

import dto.CurrencyRequest;

public class CurrencyValidator {

    public ValidationResult validate(CurrencyRequest request){
        ValidationResult result = new ValidationResult();

        if(request == null){
            result.addError("Ошибка ввода данных");
            return result;
        }

        String code = ValidationUtils.normalizeAndValidate(request.getCode(), "code", result);
        if (code != null) {
            if (code.length() != 3) {
                result.addError("Код валюты должен содержать ровно 3 символа");
            } else if (!ValidationUtils.isValidCurrencyCode(code)) {
                result.addError("Код валюты должен содержать только латинские буквы");
            }
            request.setCode(code);
        }

        String name = ValidationUtils.normalizeAndValidate(request.getName(), "name", result);
        if (name != null) {
            if (name.length() < 2 || name.length() > 100) {
                result.addError("Название валюты должно быть от 2 до 100 символов");
            }
            request.setName(name);
        }

        String sign = ValidationUtils.normalizeAndValidate(request.getSign(), "sign", result);
        if (sign != null) {
            if (!ValidationUtils.isValidSign(sign)) {
                result.addError("Символ валюты не должен превышать 1 символа");
            }
            request.setSign(sign);
        }
        return result;
    }

    public ValidationResult validateCode(String code) {
        ValidationResult result = new ValidationResult();
        String normalized = ValidationUtils.validateAndNormalizeCode(code, result);
        if (normalized == null) {
            return result;
        }
        return result;
    }
}
