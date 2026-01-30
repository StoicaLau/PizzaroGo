package com.pizzaro_go.user.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.user.dtos.UserRequest;
import com.pizzaro_go.user.dtos.UserResponse;
import com.pizzaro_go.user.entity.UserEntity;
import com.pizzaro_go.user.exceptions.UserNotFoundException;
import com.pizzaro_go.user.mapper.IUserMapper;
import com.pizzaro_go.user.repository.IUserRepository;
import com.pizzaro_go.user.resources.PasswordUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for user operations.
 */
@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserMapper userMapper;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Registers a new user.
     *
     * @param user the request containing user details
     * @return a MessageResponse with success or error information
     * @throws PGException if a repository error occurs during registration
     */
    public MessageResponse register(UserRequest user) throws PGException {
        this.log.info("Register the user: {}", user.getUsername());
        try {
            if (this.userRepository.existsByUsername(user.getUsername())) {
                return new MessageResponse("The user name already exists.");
            }
            if (this.userRepository.existsByEmail(user.getEmail())) {
                return new MessageResponse("The email already exists.");
            }

            UserEntity userToSave = this.userMapper.toEntity(user);
            userToSave.setPassword(PasswordUtils.encryptPassword(user.getPassword()));
            UserEntity savedUser = this.userRepository.save(userToSave);
            return new MessageResponse(savedUser.getId().toString());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to register the user: ";
            this.log.error(errorMsg, e);

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
            this.log.info("Retrieving user with email: {}", email);
            Optional<UserEntity> user = this.userRepository.findByEmail(email);
            if (user.isPresent()) {
                return this.userMapper.toResponse(user.get());
            } else {
                String errorMsg = "Could not find any user with email: " + email;
                log.error(errorMsg);
                throw new UserNotFoundException(errorMsg);
            }

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get the user by email " + email + ": ";
            log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }

    }

    /**
     * Authenticates a user.
     *
     * @param loginRequest the login credentials
     * @return UserResponse if authentication is successful
     * @throws PGException if a repository error occurs
     */
    public UserResponse login(UserRequest loginRequest) throws PGException {
        try {

            String email = loginRequest.getEmail() != null ? loginRequest.getEmail().trim() : "";
            String rawPassword = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : "";

            this.log.info("Login request received for email: [{}]", email);


            Optional<UserEntity> userOptional = this.userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                String dbHash = user.getPassword();

                boolean isPasswordCorrect = PasswordUtils.verifyPassword(rawPassword, dbHash);

                if (isPasswordCorrect) {
                    this.log.info("LOGIN SUCCESS for user [{}]", email);
                    return this.userMapper.toResponse(user);
                } else {
                    this.log.warn("LOGIN FAILED: Wrong password for user [{}]", email);
                    throw new PGException("Invalid credentials.");
                }
            } else {
                this.log.warn("LOGIN FAILED: No user found with email [{}]", email);
                throw new PGException("Invalid credentials.");
            }

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred during login: ";
            log.error(errorMsg, e);
            throw new PGException(errorMsg + e.getMessage());
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
            List<UserEntity> users = userRepository.findAll();
            return users.stream()
                    .map(this.userMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get all users: ";
            log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates the status (Role) of an existing user.
     *
     * @param userRequest the request containing the user ID and the new role
     * @return a MessageResponse with the result
     * @throws PGException if a repository error occurs or user is not found
     */
    public MessageResponse updateStatus(UserRequest userRequest) throws PGException {
        try {
            Long id = userRequest.getId();
            log.info("Updating status for user ID: {}", id);

            Optional<UserEntity> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                if (userRequest.getRole() != null) {
                    user.setRole(com.pizzaro_go.common.enums.Role.valueOf(userRequest.getRole().toUpperCase()));
                }
                userRepository.save(user);
                return new MessageResponse("User status updated successfully.");
            } else {
                throw new UserNotFoundException("User not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error updating user status", e);
            throw new PGException("Failed to update user status: " + e.getMessage());
        }
    }
}
          

                
                    
                    
                    

                