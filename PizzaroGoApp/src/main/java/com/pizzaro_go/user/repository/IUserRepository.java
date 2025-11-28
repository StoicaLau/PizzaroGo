package com.pizzaro_go.user.repository;

import com.pizzaro_go.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * User repository interface
 */
public interface IUserRepository extends JpaRepository<User,Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User getByEmail(String email);
    List<User> getAllUsers();
}
