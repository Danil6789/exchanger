package mapper;

import dto.ExchangeResponseDto;
import model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "default")
public interface ExchangeMapper {

    @Mapping(target="convertedAmount", expression = "java(amount.multiply(exchangeRate.getRate()))")
    ExchangeResponseDto toDto(ExchangeRate exchangeRate, BigDecimal amount);
}
