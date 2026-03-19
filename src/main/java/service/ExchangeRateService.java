package service;

import model.ExchangeRate;
import repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepo;
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepo){
        this.exchangeRateRepo = exchangeRateRepo;
    }

    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode){
        Optional<ExchangeRate> exchangeRateOptAB = exchangeRateRepo.findByCodes(baseCurrencyCode, targetCurrencyCode);
        if(exchangeRateOptAB.isPresent()){
            return exchangeRateOptAB.get();
        }

        Optional<ExchangeRate> exchangeRateOptBA = exchangeRateRepo.findByCodes(targetCurrencyCode, baseCurrencyCode);
        if(exchangeRateOptBA.isPresent()){
            var exchangeRate = exchangeRateOptBA.get();
            exchangeRate.setRate(BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP));
            return exchangeRate;
        }

        Optional<ExchangeRate> exchangeRateOptA = exchangeRateRepo.findByCodes("USD", baseCurrencyCode);
        if(exchangeRateOptA.isPresent()){
            Optional<ExchangeRate> exchangeRateOptB = exchangeRateRepo.findByCodes("USD", targetCurrencyCode);
            if(exchangeRateOptB.isPresent()){
                BigDecimal rate = exchangeRateOptB.get().getRate().divide(exchangeRateOptA.get().getRate(), 2, RoundingMode.HALF_UP);
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setRate(rate);
                exchangeRate.setBaseCurrency(exchangeRateOptA.get().getTargetCurrency());
                exchangeRate.setTargetCurrency(exchangeRateOptB.get().getTargetCurrency());
                exchangeRate.setId(0L);
                return exchangeRate;
            }
        }

        throw new RuntimeException("Невозможно найти курс для " + baseCurrencyCode + "/" + targetCurrencyCode);
    }
}
