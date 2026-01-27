package com.pizzaro_go.menu_product.dtos;

import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuProductResponse {
    private Long id;
    private String name;
    private String imageURL;
    private String description;
    private String productCategory;
    private Double price;
    private List<ProductStockUsageResponse> productStockUsageResponses;
}
