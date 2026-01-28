package com.pizzaro_go.oreder_item.repository;

import com.pizzaro_go.oreder_item.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for OrderItem entity.
 */
@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Deletes all order items associated with a specific order.
     *
     * @param orderId the ID of the order
     */
    void deleteByOrderId(Long orderId);
}
