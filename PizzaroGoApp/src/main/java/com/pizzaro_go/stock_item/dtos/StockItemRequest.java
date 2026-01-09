package com.pizzaro_go.stock_item.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockItemRequest {
    private Long id;
    private String name;
    private String category;
    private Double quantity;
    private String unit;

}
