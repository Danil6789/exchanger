package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import repositories.CurrencyRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
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
            String code = req.getPathInfo().substring(1);
            if(code.length() != 3){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Валюта не найдена");
                objectMapper.writeValue(resp.getOutputStream(), error);
                return;
            }
            Optional<Currency> currencyOpt = currencyRepo.findByCode(code);
            if(currencyOpt.isPresent()){
                resp.setStatus(200);
                objectMapper.writeValue(resp.getOutputStream(), currencyOpt.get());
            }
            else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Код валюты отсутствует в адресе");
                objectMapper.writeValue(resp.getOutputStream(), error);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            objectMapper.writeValue(resp.getOutputStream(), error);
        }
    }
}
