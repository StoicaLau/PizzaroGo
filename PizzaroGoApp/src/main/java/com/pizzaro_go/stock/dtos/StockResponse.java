package com.pizzaro_go.stock.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockResponse {

    private Long id;
    private String name;
    private Double quantity ;
    private String unit;
}
