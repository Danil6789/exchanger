package service;

import dto.ExchangeRateRequest;
import dto.ExchangeRateResponse;
import exception.ExchangeRateNotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.ExchangeRateMapper;
import repository.ExchangeRateRepository;

import java.util.List;

@RequiredArgsConstructor
public class ExchangeRateService {
    private final ExchangeRateMapper exchangeRateMapper;
    private final ExchangeRateRepository exchangeRateRepo;

    public List<ExchangeRateResponse> getAllExchangeRate(){
        return exchangeRateMapper.toDtoList(exchangeRateRepo.findAll());
    }

    public ExchangeRateResponse addExchangeRate(ExchangeRateRequest exchangeRateDto){
        return exchangeRateMapper.toDto(
                exchangeRateRepo.save(
                        new ExchangeRateRequest(
                            exchangeRateDto.getBaseCurrencyCode(),
                            exchangeRateDto.getTargetCurrencyCode(),
                            exchangeRateDto.getRate())));
    }

    public ExchangeRateResponse getExchangeRate(String baseCurrencyCode, String targetCurrencyCode){
        return exchangeRateMapper
                .toDto(exchangeRateRepo.findByCodes(baseCurrencyCode, targetCurrencyCode)
                        .orElseThrow(
                                ()-> new ExchangeRateNotFoundException("Обменный курс для пары не найден")
        ));
    }

    public ExchangeRateResponse updateRate(ExchangeRateRequest exchangeRateDto){
        return exchangeRateMapper.toDto( exchangeRateRepo.updateRate(exchangeRateDto.
                getBaseCurrencyCode(),
                exchangeRateDto.getTargetCurrencyCode(),
                exchangeRateDto.getRate()));
    }
}
