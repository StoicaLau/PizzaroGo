package com.pizzaro_go.user.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.entity.User;
import com.pizzaro_go.user.repository.IUserRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * User Service
 */
@Service
public class UserService {

    private final IUserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public MessageResponse register(UserRequest user) throws RuntimeException {
        log.info("Register the user: {}", user.getUsername());
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                return new MessageResponse("The user name already exists.");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                return new MessageResponse("The email already exists.");
            }

            User userToSave = userRepository.save(user.toUser());
            return new MessageResponse(userToSave.getId().toString());

        } catch (RuntimeException e) {
            String errorMsg = "Error occurred when trying to register the user: ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new RuntimeException(errorMsg);

        }
    }

    public UserResponse getByEmail(String email) throws RuntimeException {
        try {
            log.info("Retrieving user with email: " + email);
            if (userRepository.existsByEmail(email)) {
                User user = userRepository.getByEmail(email);
                return new UserResponse(user);
            } else {
                String errorMsg = "Could not find any user with email: " + email;
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

        } catch (RuntimeException e) {
            String errorMsg = "Error occurred when trying to get the user by email " + email + ": ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new RuntimeException(errorMsg);
        }

    }

    public List<UserResponse> getAll() throws RuntimeException {
        try {
            log.info("Retrieving all users");
            List<User> users = userRepository.getAllUsers();
            return users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            String errorMsg = "Error occurred when trying to get all users: ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new RuntimeException(errorMsg);

        }

    }
}
