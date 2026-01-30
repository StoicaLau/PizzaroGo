package com.pizzaro_go.user.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param userRequest the registration details
     * @return MessageResponse with a message
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public MessageResponse register(@RequestBody UserRequest userRequest) {
        return this.userService.register(userRequest);
    }

    /**
     * Authenticates a user.
     *
     * @param loginRequest the login credentials
     * @return UserResponse with user details
     */
    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public UserResponse login(@RequestBody UserRequest loginRequest) {
        return this.userService.login(loginRequest);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the user's email address
     * @return UserResponse with user details
     */
    @GetMapping("/{email}")
    @Operation(summary = "Retrieve a user by email")
    public UserResponse getByEmail(@PathParam("email") String email) {
        return this.userService.getByEmail(email);
    }

    /**
     * Retrieves all users.
     *
     * @return list of UserResponse objects
     */
    @GetMapping("")
    @Operation(summary = "Retrieve all users")
    public List<UserResponse> getAll() {
        return this.userService.getAll();
    }

    /**
     * Updates the status (Role) of an existing user.
     *
     * @param userRequest the request containing the user ID and the new role
     * @return MessageResponse with the result
     */
    @PatchMapping("/status")
    @Operation(summary = "Update user status")
    public MessageResponse updateStatus(@RequestBody UserRequest userRequest) {
        return this.userService.updateStatus(userRequest);
    }
}
