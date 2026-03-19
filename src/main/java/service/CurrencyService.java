package service;

import dto.CurrencyRequest;
import dto.CurrencyResponse;
import exception.CurrencyNotFoundException;
import lombok.RequiredArgsConstructor;
import mapper.CurrencyMapper;
import model.Currency;
import repository.CurrencyRepository;

@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepo;
    private final CurrencyMapper currencyMapper;

    public CurrencyResponse getCurrency(String code){
        return currencyRepo.findByCode(code)
                .map(currencyMapper::toDto)
                .orElseThrow(()-> new CurrencyNotFoundException("Такой валюты нет"));
        //TODO: проверить возможно нужно эту в сервлет запихнуть и сервис тогде не нужен раз 1 строчка
    }

    public CurrencyResponse addCurrency(CurrencyRequest currencyRequest){
       Currency currency = currencyMapper.toEntity(currencyRequest);
        return currencyMapper.toDto(currencyRepo
                .save(currency));
        //TODO: проверить возможно нужно эту в сервлет запихнуть и сервис тогде не нужен раз 1 строчка
    }
}
