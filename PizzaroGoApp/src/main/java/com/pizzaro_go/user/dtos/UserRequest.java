package com.pizzaro_go.user.dtos;

import com.pizzaro_go.order.dtos.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String role;
    private List<OrderRequest> orders;

}
