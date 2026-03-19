package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private CurrencyService currencyService;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyService = (CurrencyService)getServletContext()
                .getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().substring(1);
        if(code.length() != 3){//TODO: сделать нормальную валидацию через специальные кастомные классы
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Валюта не найдена");
            objectMapper.writeValue(resp.getOutputStream(), error);
            return;
        }

        CurrencyResponse currencyResponse = currencyService.getCurrency(code);
        objectMapper.writeValue(resp.getOutputStream(), currencyResponse);
    }
}
