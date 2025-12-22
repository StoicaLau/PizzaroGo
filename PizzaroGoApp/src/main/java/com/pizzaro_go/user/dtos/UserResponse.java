package com.pizzaro_go.user.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;

}
