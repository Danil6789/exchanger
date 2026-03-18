package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateResponse {
    private Long id;
    private CurrencyResponse baseCurrency;
    private CurrencyResponse targetCurrency;
    private BigDecimal rate;
}
