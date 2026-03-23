package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateRequest;
import dto.ExchangeRateResponse;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import validator.ExchangeRateValidator;
import validator.ValidationResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet{
    private ObjectMapper objectMapper;
    private ExchangeRateService exchangeRateService;
    private ExchangeRateValidator exchangeRateValidator;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String method = req.getMethod();

        if (method.equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateService = (ExchangeRateService) getServletContext()
                .getAttribute("exchangeRateService");
        exchangeRateValidator = new ExchangeRateValidator();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathCode = req.getPathInfo();

        ValidationResult validation = exchangeRateValidator.validateAndParsePath(pathCode);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getFirstError());
        }

        String baseCode = validation.getData("baseCode");
        String targetCode = validation.getData("targetCode");

        ExchangeRateResponse response = exchangeRateService.getExchangeRate(baseCode, targetCode);
        resp.setStatus(200);
        objectMapper.writeValue(resp.getOutputStream(), response);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathCode = req.getPathInfo();
        ValidationResult validation = exchangeRateValidator.validateAndParsePath(pathCode);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getFirstError());
        }
        String rate = getParameterFromBody(req, "rate");

        String baseCode = validation.getData("baseCode");
        String targetCode = validation.getData("targetCode");

        ValidationResult rateValidation = exchangeRateValidator.validateRate(rate);
        if (!rateValidation.isValid()) {
            throw new ValidationException(rateValidation.getFirstError());
        }

        ExchangeRateRequest request = new ExchangeRateRequest(baseCode, targetCode, new BigDecimal(rate));
        ExchangeRateResponse response = exchangeRateService.updateRate(request);
        objectMapper.writeValue(resp.getOutputStream(), response);
    }

    private String getParameterFromBody(HttpServletRequest req, String paramName) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining("\n"));
        if (body == null || body.isEmpty()) {
            return null;
        }

        String[] params = body.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}
