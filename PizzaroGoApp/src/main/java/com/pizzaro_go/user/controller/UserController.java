package com.pizzaro_go.user.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userservice){
        this.userService=userservice;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<MessageResponse> register(@RequestBody UserRequest userRequest){
        MessageResponse response=this.userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
