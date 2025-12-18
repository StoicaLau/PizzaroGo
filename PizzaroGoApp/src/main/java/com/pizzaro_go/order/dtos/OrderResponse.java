package com.pizzaro_go.order.dtos;

import com.pizzaro_go.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private String userid;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedAt;
    private String status;
    private Double orderPrice;
    private Double deliveryPrice;
    private Double totalPrice;

    public OrderResponse(Order order) {
        this.id = String.valueOf(order.getId());
        this.userid = String.valueOf(order.getUser().getId());
        this.createdAt = order.getCreatedAt();
        this.estimatedAt = order.getEstimatedAt();
        this.status = String.valueOf(order.getStatus());
        this.orderPrice = order.getOrderPrice();
        this.deliveryPrice = order.getDeliveryPrice();
        this.totalPrice = order.getTotalPrice();
    }

}
