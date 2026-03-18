package mapper;

import dto.CurrencyRequestDto;
import dto.CurrencyResponseDto;
import model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface CurrencyMapper {

    @Mapping(source = "fullName", target = "name")
    Currency toEntity(CurrencyRequestDto request);

    @Mapping(source = "name", target = "fullName")
    CurrencyResponseDto toDto(Currency currency);
}
