package model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ExchangeRate {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
