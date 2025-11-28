package com.pizzaro_go.user.dtos;

import com.pizzaro_go.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String Role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.password = user.getPhone();
        this.Role = user.getRole().name();

    }
}
