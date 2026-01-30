package com.pizzaro_go.oreder_item.repository;

import com.pizzaro_go.order.entity.OrderEntity;
import com.pizzaro_go.oreder_item.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for OrderItem entity.
 */
@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    /**
     * Deletes all order items associated with a specific order.
     *
     * @param orderId the ID of the order
     */
    void deleteByOrderId(Long orderId);

    /**
     * Finds an order by its id and eagerly fetches its associated order items
     * and their menu products.
     *
     * @param id the id of the order
     * @return an Optional containing the order if found, or empty otherwise
     */
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.menuProduct " +
            "WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithOrderItems(Long id);
}
