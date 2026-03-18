package dto;

import lombok.Data;

@Data
public class CurrencyResponse {
    private Long id;
    private String name;
    private String code;
    private String sign;
}
