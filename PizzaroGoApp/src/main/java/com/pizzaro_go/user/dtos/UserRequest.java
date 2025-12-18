package com.pizzaro_go.user.dtos;


import com.pizzaro_go.common.enums.Role;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.entity.Order;
import com.pizzaro_go.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String role;
    private List<OrderRequest> orders;

    public User toUser() {
        Integer idToSet=Integer.parseInt(this.id);
        Role userRole = this.role != null ? Role.valueOf(this.role) : Role.CUSTOMER;
        List<Order> ordersToSet = this.orders != null ? this.orders.stream().map(OrderRequest::toOrder).collect(Collectors.toList()):null;
        return new User(idToSet, this.username, this.email, this.phone, this.password, userRole,ordersToSet);
    }

}
