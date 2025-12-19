package com.pizzaro_go.order.dtos;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.order.entity.Order;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
//TODO MAPSTRUCT
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private Long id;
    private UserRequest user;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedAt;
    private String status;
    private Double orderPrice;
    private Double deliveryPrice;
    private Double totalPrice;

    public Order toOrder() {
        User userToSet = this.user.toUser();
        Status statusToSet = this.status != null ? Status.valueOf(status.toUpperCase()) : Status.PENDING;
        return new Order(this.id, userToSet, this.createdAt, this.estimatedAt, statusToSet, this.orderPrice, this.deliveryPrice, this.totalPrice);
    }

}
