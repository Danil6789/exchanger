package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRateRequest {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
}
