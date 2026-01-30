package com.pizzaro_go.order.repository;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entities.
 * Provides custom query methods for accessing orders.
 */
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of orders for the given user
     * @throws RepositoryException if a data access error occurs
     */
    List<OrderEntity> getAllByUserId(Long userId) throws RepositoryException;

    /**
     * Finds an order by its ID and eagerly fetches its associated order items
     * and their menu products.
     *
     * @param id the ID of the order
     * @return an Optional containing the order if found, or empty otherwise
     */
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.menuProduct " +
            "WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithOrderItems(Long id);

    /**
     * Finds all orders that have statuses in the provided list.
     *
     * @param statuses the list of statuses to filter by
     * @return a list of orders matching any of the given statuses
     */
    List<OrderEntity> findAllByStatusIn(List<Status> statuses);
}
