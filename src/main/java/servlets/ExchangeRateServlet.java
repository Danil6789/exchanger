package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import repositories.CurrencyRepository;
import repositories.ExchangeRateRepository;
import jakarta.servlet.http.HttpServlet;
import services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet{
    private ObjectMapper objectMapper;
    private ExchangeRateRepository exchangeRateRepo;
    private ExchangeRateService exchangeRateService;
    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.exchangeRateRepo = (ExchangeRateRepository) getServletContext()
                .getAttribute("exchangeRateRepo");
        this.exchangeRateService = new ExchangeRateService(exchangeRateRepo);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try{
            String pathCode = req.getPathInfo();
            if(pathCode == null || pathCode.equals("/")){
                resp.setStatus(400);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Валюты пары отсутствуют в адресе");
                objectMapper.writeValue(resp.getOutputStream(), error);
                return;
            }
            pathCode = pathCode.substring(1);
            if(pathCode.length() == 6){
                String baseCurrencyCode = pathCode.substring(0, 3);
                String targetCurrencyCode = pathCode.substring(3, 6);
                ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);

                if(exchangeRate != null){
                    resp.setStatus(200);
                    objectMapper.writeValue(resp.getOutputStream(), exchangeRate);
                }
                else{
                    resp.setStatus(404);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Обменный курс для пары не найден");
                    objectMapper.writeValue(resp.getOutputStream(), error);
                }
            }
            else{
                resp.setStatus(400);
                Map<String, String> error = new HashMap<>();
                objectMapper.writeValue(resp.getOutputStream(), error.put("error", "Некорректный обменный курс"));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            objectMapper.writeValue(resp.getOutputStream(), error);
        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            String pathCode = req.getPathInfo();
            if(pathCode == null || pathCode.equals("/")){
                resp.setStatus(400);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Валюты пары отсутствуют в адресе");
                objectMapper.writeValue(resp.getOutputStream(), error);
                return;
            }
            pathCode = pathCode.substring(1);
            if(pathCode.length() == 6) {
                String baseCurrencyCode = pathCode.substring(0, 3);
                String targetCurrencyCode = pathCode.substring(3, 6);
                String rate = req.getParameter("rate");
                ExchangeRate exchangeRate = exchangeRateRepo.updateRate(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate));
                objectMapper.writeValue(resp.getOutputStream(), exchangeRate);
            }
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            objectMapper.writeValue(resp.getOutputStream(), error);
        }


    }
}
