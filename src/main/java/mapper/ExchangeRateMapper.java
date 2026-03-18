package mapper;

import dto.ExchangeRateRequestDto;
import dto.ExchangeRateResponseDto;
import model.ExchangeRate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "default")
public interface ExchangeRateMapper {
    ExchangeRate toEntity(ExchangeRateRequestDto request);
    ExchangeRateResponseDto toDto(ExchangeRate exchangeRate);
}
