package dto;

import java.math.BigDecimal;

public class ExchangeRateResponseDto {
    private Long id;
    private CurrencyResponseDto baseCurrency;
    private CurrencyResponseDto targetCurrency;
    private BigDecimal rate;
}
