package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CurrencyRequest {
    private String name;
    private String code;
    private String sign;
}
