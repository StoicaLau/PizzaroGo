package com.pizzaro_go.product_stock_usage.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockUsageResponse {
    private Long id;
    private Long menuProductId;
    private Long stockItemId;
    private Double quantityPerUnit;
}
