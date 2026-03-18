package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import models.Currency;
import repositories.CurrencyRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@WebServlet(urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private CurrencyRepository currencyRepo;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyRepo = (CurrencyRepository) getServletContext()
                .getAttribute("currencyRepo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            List<Currency> currencies = currencyRepo.findAll();
            objectMapper.writeValue(resp.getOutputStream(), currencies);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            objectMapper.writeValue(resp.getOutputStream(), error);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        // Проверяем, что все поля есть
        if (name == null || code == null || sign == null) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Не все поля заполнены");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        // Проверяем, существует ли уже такая валюта
        if (currencyRepo.findByCode(code).isPresent()) {
            resp.setStatus(409);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Такая валюта уже есть");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        // Создаем и сохраняем валюту
        Currency currency = new Currency();
        currency.setName(name);
        currency.setCode(code.toUpperCase());
        currency.setSign(sign);

        Currency saved = currencyRepo.save(currency);

        // Отправляем ответ
        resp.setStatus(201);
        objectMapper.writeValue(resp.getOutputStream(), saved);
    }
}
