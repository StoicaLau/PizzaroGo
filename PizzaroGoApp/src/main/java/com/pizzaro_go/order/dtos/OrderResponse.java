package com.pizzaro_go.order.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.pizzaro_go.order_item.dtos.OrderItemResponse;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<OrderItemResponse> orderItems;

}
