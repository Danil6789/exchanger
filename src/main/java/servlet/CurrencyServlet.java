package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyResponse;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import validator.CurrencyValidator;
import validator.ValidationResult;

import java.io.IOException;

@WebServlet(urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private CurrencyService currencyService;
    private CurrencyValidator currencyValidator;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyService = (CurrencyService)getServletContext()
                .getAttribute("currencyService");
        currencyValidator = new CurrencyValidator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ValidationException("Код валюты отсутствует в адресе");
        }
        String code = pathInfo.substring(1);

        ValidationResult validation = currencyValidator.validateCode(code);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getFirstError());
        }

        code = code.trim().toUpperCase();
        CurrencyResponse currencyResponse = currencyService.getCurrency(code);
        objectMapper.writeValue(resp.getOutputStream(), currencyResponse);
    }
}
