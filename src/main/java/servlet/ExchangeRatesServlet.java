package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateRequest;
import dto.ExchangeRateResponse;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import service.ExchangeRateService;
import validator.ExchangeRateValidator;
import validator.ValidationResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@WebServlet(urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private ExchangeRateService exchangeRateService;
    private ExchangeRateValidator exchangeRateValidator;
    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRateService");
        exchangeRateValidator = new ExchangeRateValidator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponse> exchangeRateResponses = exchangeRateService.getAllExchangeRate();
        resp.setStatus(200);
        objectMapper.writeValue(resp.getOutputStream(), exchangeRateResponses);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        ValidationResult validation = exchangeRateValidator.validateCreateRequest(
                baseCurrencyCode,
                targetCurrencyCode,
                rateParam
        );

        if (!validation.isValid()) {
            throw new ValidationException(validation.getFirstError());
        }

        String baseCode = validation.getData("baseCurrencyCode");
        String targetCode = validation.getData("targetCurrencyCode");
        BigDecimal rate = validation.getData("rate");

        ExchangeRateRequest request = new ExchangeRateRequest(baseCode, targetCode, rate);
        ExchangeRateResponse response = exchangeRateService.addExchangeRate(request);

        resp.setStatus(201);
        objectMapper.writeValue(resp.getOutputStream(), response);
    }
}
