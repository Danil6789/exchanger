package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyRequest;
import dto.CurrencyResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import mapper.CurrencyMapper;
import model.Currency;
import repository.CurrencyRepository;
import service.CurrencyService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@WebServlet(urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private CurrencyRepository currencyRepo;
    private CurrencyMapper currencyMapper;
    private CurrencyService currencyService;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyRepo = (CurrencyRepository) getServletContext()
                .getAttribute("currencyRepo");
        currencyMapper = (CurrencyMapper) getServletContext().getAttribute("currencyMapper");
        currencyService = (CurrencyService) getServletContext().getAttribute("currentService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyRepo.findAll();
        List<CurrencyResponse> currenciesResponses = currencyMapper.toDtoList(currencies);
        objectMapper.writeValue(resp.getOutputStream(), currenciesResponses);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        CurrencyRequest currencyRequest = new CurrencyRequest(name, code, sign);
        if (name == null || name.isEmpty() ||//TODO: Сделать валидацию классами
                code == null || code.isEmpty() ||
                sign == null || sign.isEmpty()) {

            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Не все поля заполнены");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        CurrencyResponse currencyResponse = currencyService.addCurrency(currencyRequest);
        resp.setStatus(201);
        objectMapper.writeValue(resp.getOutputStream(), currencyResponse);
    }
}