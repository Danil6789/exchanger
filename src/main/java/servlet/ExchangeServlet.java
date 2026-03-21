package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeResponse;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.ExchangeMapper;
import repository.ExchangeRateRepository;
import service.ConvertService;
import validator.ExchangeRateValidator;
import validator.ValidationResult;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet{
    private ObjectMapper objectMapper;
    private ExchangeMapper exchangeMapper;
    private ConvertService convertService;
    private ExchangeRateValidator exchangeRateValidator;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        ExchangeRateRepository exchangeRateRepo = (ExchangeRateRepository) getServletContext()
                .getAttribute("exchangeRateRepo");
        this.convertService = new ConvertService(exchangeRateRepo);
        this.exchangeMapper = (ExchangeMapper) getServletContext().getAttribute("exchangeMapper");
        exchangeRateValidator = new ExchangeRateValidator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== EXCHANGE REQUEST DIAGNOSTICS ===");
        System.out.println("from parameter: " + req.getParameter("from"));
        System.out.println("to parameter: " + req.getParameter("to"));
        System.out.println("amount parameter: " + req.getParameter("amount"));

        // Все параметры
        req.getParameterMap().forEach((key, values) -> {
            System.out.println("  " + key + " = " + String.join(", ", values));
        });
        System.out.println("=== END DIAGNOSTICS ===");
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        ValidationResult validation = exchangeRateValidator.validateExchangeRequest(baseCurrencyCode, targetCurrencyCode, amountParam);

        if (!validation.isValid()) {
            throw new ValidationException(validation.getFirstError());
        }

        String baseCode = validation.getData("baseCode");
        String targetCode = validation.getData("targetCode");
        BigDecimal amount = validation.getData("amount");

        var exchangeRate = convertService.getExchangeRate(baseCode, targetCode);
        ExchangeResponse exchangeResponse = exchangeMapper.toDto(exchangeRate, amount);

        objectMapper.writeValue(resp.getOutputStream(), exchangeResponse);
    }
}
