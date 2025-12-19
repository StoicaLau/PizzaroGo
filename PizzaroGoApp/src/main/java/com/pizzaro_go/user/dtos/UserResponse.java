package com.pizzaro_go.user.dtos;

import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String Role;
    private List<OrderResponse> orders;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.Role = user.getRole().name();
        this.orders = user.getOrders() != null ? user.getOrders().stream().map(OrderResponse::new).collect(Collectors.toList()) : null;

    }
}
