package com.pizzaro_go.user.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<MessageResponse> register(@RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(userRequest));
    }

    @Autowired
    private jakarta.servlet.http.HttpSession httpSession;

    /**
     * Authenticates a user and starts a session.
     *
     * @param loginRequest the login credentials
     * @return a ResponseEntity with UserResponse
     */
    @PostMapping("/login")
    @Operation(summary = "Login a user and start session")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest loginRequest) {
        UserResponse user = this.userService.login(loginRequest);
        httpSession.setAttribute("loggedUser", user);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves the currently logged-in user from the session.
     *
     * @return a ResponseEntity with UserResponse or UNAUTHORIZED
     */
    @GetMapping("/me")
    @Operation(summary = "Get currently logged in user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = (UserResponse) httpSession.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Terminates the current session.
     *
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout the user")
    public ResponseEntity<MessageResponse> logout() {
        httpSession.invalidate();
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the user's email address
     * @return a ResponseEntity with UserResponse
     */
    @GetMapping("/{email}")
    @Operation(summary = "Retrieve a user by email")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(this.userService.getByEmail(email));
    }

    /**
     * Retrieves all users.
     *
     * @return a ResponseEntity with a list of UserResponse objects
     */
    @GetMapping("")
    @Operation(summary = "Retrieve all users")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(this.userService.getAll());
    }

    /**
     * Updates the status (Role) of an existing user.
     *
     * @param userRequest the request containing the user ID and the new role
     * @return a ResponseEntity with MessageResponse
     */
    @PatchMapping("/status")
    @Operation(summary = "Update user status")
    public ResponseEntity<MessageResponse> updateStatus(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(this.userService.updateStatus(userRequest));
    }
}
