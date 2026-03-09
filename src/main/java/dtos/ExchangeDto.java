package dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Currency;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class ExchangeDto {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
