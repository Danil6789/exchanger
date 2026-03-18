package dto;

import lombok.Data;

@Data
public class CurrencyResponseDto {
    private Long id;
    private String name;
    private String code;
    private String sign;
}
