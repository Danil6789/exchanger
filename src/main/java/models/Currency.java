package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Currency {
    private Long id;
    private String code;
    private String name;
    private String sign;
}
