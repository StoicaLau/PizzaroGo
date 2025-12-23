package com.pizzaro_go.order.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedAt;
    private String status;
    private Double orderPrice;
    private Double deliveryPrice;
    private Double totalPrice;

}
