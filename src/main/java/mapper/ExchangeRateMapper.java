package mapper;

import dto.ExchangeRateRequest;
import dto.ExchangeRateResponse;
import model.Currency;
import model.ExchangeRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "default", uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    default ExchangeRate toEntity(ExchangeRateRequest request, Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(request.getRate());
        return exchangeRate;
    }

    ExchangeRateResponse toDto(ExchangeRate exchangeRate);

    List<ExchangeRateResponse> toDtoList(List<ExchangeRate> exchangeRates);
}
