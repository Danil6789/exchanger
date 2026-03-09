package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.ExchangeDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.ExchangeMapper;
import org.mapstruct.factory.Mappers;
import repositories.ExchangeRateRepository;
import jakarta.servlet.http.HttpServlet;
import services.ExchangeRateService;

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
        this.exchangeMapper = Mappers.getMapper(ExchangeMapper.class);
        this.exchangeRateService = new ExchangeRateService(exchangeRateRepo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        var exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
        ExchangeDto exchangeDto = exchangeMapper.toDto(exchangeRate, amount);

        objectMapper.writeValue(resp.getOutputStream(), exchangeDto);
    }
}
