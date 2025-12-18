package com.pizzaro_go.order.repository;

import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Order entities.
 * Provides custom query methods for accessing orders.
 */
public interface IOrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of orders for the given user
     * @throws RepositoryException if a data access error occurs
     */
    List<Order> getAllByUserId(Integer userId) throws RepositoryException;
}
