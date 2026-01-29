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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for user operations.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Registers a new user.
     *
     * @param user the request containing user details
     * @return an AuthenticationResponse with the token and user details
     * @throws PGException if a repository error occurs during registration
     */
    public AuthenticationResponse register(UserRequest user) throws PGException {
        this.log.info("Register the user: {}", user.getUsername());
        try {
            if (this.userRepository.existsByUsername(user.getUsername())) {
                throw new PGException("The username already exists.");
            }
            if (this.userRepository.existsByEmail(user.getEmail())) {
                throw new PGException("The email already exists.");
            }

     *                     
            UserEntity userToSave = this.userMapper.toEntity(user);
            userToSave.setPassword(passwordEncoder.encode(user.getPassword()));
            UserEntity savedUser = this.userRepository.save(userToSave);

            String jwtToken = jwtService.generateToken(savedUser);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
             

            
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to register the user: ";
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg + e.getMessage());
        }
    }


            eves a user by email.
     *
     * @param email the email address of the user
     * @return a UserResponse with user details
     * @throws PGException           if a repository error occurs
     * @throws UserNotFoundException if no user is found with the gi

            serResponse getByEmail(String email) throws PGException {
        try {
            this.log.info("Retrieving user with email: {}", email);
            Optional<UserEntity> user = this.userRepository.getByEmail(email);
            if (user.isPresent()) {
                return this.userMapper.toResponse(user.get());
            } else {    String errorMsg = "Could not find any user with email: " + email;
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
     * @return AuthenticationResponse if authentication is successful
     * @throws PGException if a repository error occurs or authentication fails
     */
    public AuthenticationResponse login(UserRequest loginRequest) throws PGException {
        try {
            this.log.info("Login request for email: {}", loginRequest.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserEntity user = this.userRepository.getByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new PGException("User not found after authentication."));

            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(userMapper.
     *                        .build();

        } catch (AuthenticationException e) {
            throw new PGException("Invalid credentials.");
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred during login: ";
            log.error(errorMsg, e

                    

    /**
     * Re

     *
     * @return a list of UserResponse objects
     * @throws PGException if a repository error occurs
     */
    public List<UserResponse> getAll() throws PGException {
        try {
            log.info("Retrieving all users");
            List<UserEntity> users = userRepository.findAll();
            return users.stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get all users: ";
            log.error(errorMsg, e);
            throw new PGException(errorMsg + e.getMessage());
        }
    }
}
