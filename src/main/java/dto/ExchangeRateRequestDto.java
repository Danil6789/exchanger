package dto;

import java.math.BigDecimal;

public class ExchangeRateRequestDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
