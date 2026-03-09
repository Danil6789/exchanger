package mappers;

import dtos.ExchangeDto;
import models.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "default")
public interface ExchangeMapper {

    @Mapping(target="convertedAmount", expression = "java(amount.multiply(exchangeRate.getRate()))")
    ExchangeDto toDto(ExchangeRate exchangeRate, BigDecimal amount);
}
