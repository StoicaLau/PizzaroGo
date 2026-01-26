package com.pizzaro_go.menu_product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
