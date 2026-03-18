package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.ExchangeMapper;
import repository.ExchangeRateRepository;
import service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet{
    private ObjectMapper objectMapper;
    private ExchangeMapper exchangeMapper;
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        ExchangeRateRepository exchangeRateRepo = (ExchangeRateRepository) getServletContext()
                .getAttribute("exchangeRateRepo");
        this.exchangeRateService = new ExchangeRateService(exchangeRateRepo);
        this.exchangeMapper = (ExchangeMapper) getServletContext().getAttribute("exchangeMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        var exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
        ExchangeResponse exchangeResponse = exchangeMapper.toDto(exchangeRate, amount);

        objectMapper.writeValue(resp.getOutputStream(), exchangeResponse);
    }
}
