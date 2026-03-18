package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import model.ExchangeRate;
import repository.CurrencyRepository;
import repository.ExchangeRateRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@WebServlet(urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private ExchangeRateRepository exchangeRateRepo;
    private CurrencyRepository currencyRepo;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateRepo = (ExchangeRateRepository) getServletContext()
                .getAttribute("exchangeRateRepo");
        this.currencyRepo = (CurrencyRepository) getServletContext()
                .getAttribute("currencyRepo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            List<ExchangeRate> exchangeRates = exchangeRateRepo.findAll();
            objectMapper.writeValue(resp.getOutputStream(), exchangeRates);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            objectMapper.writeValue(resp.getOutputStream(), error);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if(baseCurrencyCode == null || targetCurrencyCode == null || rate == null){
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Не все поля заполнены");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }
        if (exchangeRateRepo.findByCoupleCodes(baseCurrencyCode, targetCurrencyCode).isPresent()) {
            resp.setStatus(409);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Такой обменный курс уже есть");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }
        if(currencyRepo.findByCode(baseCurrencyCode).isPresent() || currencyRepo.findByCode(targetCurrencyCode).isPresent()){
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrency(currencyRepo.findByCode(baseCurrencyCode).get());
            exchangeRate.setTargetCurrency(currencyRepo.findByCode(targetCurrencyCode).get());
            exchangeRate.setRate(new BigDecimal(rate));

            ExchangeRate saved = exchangeRateRepo.save(exchangeRate);
            resp.setStatus(201);
            objectMapper.writeValue(resp.getOutputStream(), saved);
        }
    }
}