package com.pizzaro_go.oreder_item.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long orderId;
    private Long menuProductId;
    private Integer quantity;
    private Double totalPrice;
    private String menuProductName;
    private String status;
}
