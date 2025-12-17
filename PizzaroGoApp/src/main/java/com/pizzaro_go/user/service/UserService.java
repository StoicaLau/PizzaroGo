package com.pizzaro_go.user.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.entity.User;
import com.pizzaro_go.user.exceptions.UserNotFoundException;
import com.pizzaro_go.user.repository.IUserRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Service layer for user operations.
 */
@Service
public class UserService {

    private final IUserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Creates a new UserService with the given repository.
     *
     * @param userRepository the repository used for user persistence
     */
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;

    }

    /**
     * Registers a new user.
     *
     * @param user the request containing user details
     * @return a MessageResponse with success or error information
     * @throws PGException if a repository error occurs during registration
     */
    public MessageResponse register(UserRequest user) throws PGException {
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

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to register the user: ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);

        }
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email address of the user
     * @return a UserResponse with user details
     * @throws PGException           if a repository error occurs
     * @throws UserNotFoundException if no user is found with the given email
     */
    public UserResponse getByEmail(String email) throws PGException {
        try {
            log.info("Retrieving user with email: " + email);
            Optional<User> user = userRepository.getByEmail(email);
            if (user.isPresent()) {
                return new UserResponse(user.get());
            } else {
                String errorMsg = "Could not find any user with email: " + email;
                log.error(errorMsg);
                throw new UserNotFoundException(errorMsg);
            }

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get the user by email " + email + ": ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }

    }

    /**
     * Retrieves all users.
     *
     * @return a list of UserResponse objects
     * @throws PGException if a repository error occurs
     */
    public List<UserResponse> getAll() throws PGException {
        try {
            log.info("Retrieving all users");
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get all users: ";
            log.error(errorMsg + "{}", e.getMessage());

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);

        }

    }
}
