package com.pizzaro_go.user.repository;

import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

/**
 * User repository interface
 */
public interface IUserRepository extends JpaRepository<User, Long> {
    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     * @throws RepositoryException if a data access error occurs
     */
    boolean existsByUsername(String username) throws RepositoryException;

    /**
     * Checks if a user exists by email.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     * @throws RepositoryException if a data access error occurs
     */
    boolean existsByEmail(String email) throws RepositoryException;

    /**
     * Retrieves a user by email.
     *
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not
     * @throws RepositoryException if a data access error occurs
     */
    Optional<User> getByEmail(String email) throws RepositoryException;

}
