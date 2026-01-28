package com.pizzaro_go.menu_product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuProductRequest {
    private Long id;
    private String name;
    private String imageURL;
    private String description;
    private String productCategory;
    private Double price;
    private List<ProductStockUsageRequest> stockUsages;
}
