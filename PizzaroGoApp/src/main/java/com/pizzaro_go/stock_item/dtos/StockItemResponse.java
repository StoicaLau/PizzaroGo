package com.pizzaro_go.stock_item.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockItemResponse {

    private Long id;
    private String name;
    private String category;
    private Double quantity;
    private String unit;

}
