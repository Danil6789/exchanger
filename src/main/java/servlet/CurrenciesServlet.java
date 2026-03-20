package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyRequest;
import dto.CurrencyResponse;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import mapper.CurrencyMapper;
import model.Currency;
import repository.CurrencyRepository;
import service.CurrencyService;
import validator.CurrencyValidator;
import validator.ValidationResult;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@WebServlet(urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private CurrencyRepository currencyRepo;
    private CurrencyMapper currencyMapper;
    private CurrencyService currencyService;
    private CurrencyValidator currencyValidator;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        currencyRepo = (CurrencyRepository) getServletContext()
                .getAttribute("currencyRepo");
        currencyMapper = (CurrencyMapper) getServletContext().getAttribute("currencyMapper");
        currencyService = (CurrencyService) getServletContext().getAttribute("currencyService");
        currencyValidator = new CurrencyValidator();
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

        ValidationResult validation = currencyValidator.validate(currencyRequest);
        if(!validation.isValid()){
            throw new ValidationException(validation.getFirstError());
        }

        CurrencyResponse currencyResponse = currencyService.addCurrency(currencyRequest);
        resp.setStatus(201);
        objectMapper.writeValue(resp.getOutputStream(), currencyResponse);
    }
}