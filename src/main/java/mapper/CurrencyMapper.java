package mapper;

import dto.CurrencyRequest;
import dto.CurrencyResponse;
import model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "default")
public interface CurrencyMapper {

    @Mapping(source = "name", target = "fullName")
    Currency toEntity(CurrencyRequest request);

    @Mapping(source = "fullName", target = "name")
    CurrencyResponse toDto(Currency currency);

    List<CurrencyResponse> toDtoList(List<Currency> currencies);
}
