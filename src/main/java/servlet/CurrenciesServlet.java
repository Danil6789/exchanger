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

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyRepo = (CurrencyRepository) getServletContext()
                .getAttribute("currencyRepo");
        currencyMapper = (CurrencyMapper) getServletContext().getAttribute("currencyMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            List<Currency> currencies = currencyRepo.findAll();
            List<CurrencyResponse> currencyResponses = currencyMapper.toDtoList(currencies);
            objectMapper.writeValue(resp.getOutputStream(), currencyResponses);
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

        if (name == null || name.isEmpty() ||
                code == null || code.isEmpty() ||
                sign == null || sign.isEmpty()) {

            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Не все поля заполнены");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        if (currencyRepo.findByCode(code).isPresent()) {
            resp.setStatus(409);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Такая валюта уже есть");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        Currency saved = currencyRepo.save(new CurrencyRequest(name, code, sign));
        resp.setStatus(201);
        CurrencyResponse currencyResponse = currencyMapper.toDto(saved);
        objectMapper.writeValue(resp.getOutputStream(), currencyResponse);
    }
}
