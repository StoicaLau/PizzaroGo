package com.pizzaro_go.oreder_item.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import com.pizzaro_go.order.entity.OrderEntity;
import com.pizzaro_go.order.repository.IOrderRepository;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.dtos.OrderItemResponse;
import com.pizzaro_go.oreder_item.entity.OrderItemEntity;
import com.pizzaro_go.oreder_item.mapper.IOrderItemMapper;
import com.pizzaro_go.oreder_item.repository.IOrderItemRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for order item operations.
 */
@Service
@Transactional
public class OrderItemService {

    @Autowired
    private IOrderItemRepository orderItemRepository;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IMenuProductRepository menuProductRepository;
    @Autowired
    private IOrderItemMapper orderItemMapper;

    private final Logger log = LoggerFactory.getLogger(OrderItemService.class);

    /**
     * Updates the status of an existing order item.
     *
     * @param id     the ID of the order item
     * @param status the new status as a string
     * @return an OrderItemResponse with the updated order item details
     * @throws PGException if the order item is not found or a repository error
     *                     occurs
     */
    public OrderItemResponse updateStatus(Long id, String status) throws PGException {
        this.log.info("Updating status for order item #{} to {}", id, status);
        try {
            OrderItemEntity orderItem = this.orderItemRepository.findById(id)
                    .orElseThrow(() -> new PGException("Order item not found with id: " + id));

            Status newStatus = Status.valueOf(status.toUpperCase());
            orderItem.setStatus(newStatus);

            OrderItemEntity savedItem = this.orderItemRepository.save(orderItem);
            return this.orderItemMapper.toResponse(savedItem);

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating status for order item with id: " + id + " -> ";
            this.log.error(errorMsg, e);
            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        } catch (IllegalArgumentException e) {
            throw new PGException("Invalid status: " + status);
        }
    }

    /**
     * Creates a new order item.
     *
     * @param request the request containing order item details
     * @return a MessageResponse with the created order item ID
     * @throws PGException if a repository error occurs or related entities are not
     *                     found
     */
    public MessageResponse create(OrderItemRequest request) throws PGException {
        try {
            this.log.info("Create a new order item");
            OrderItemEntity orderItem = this.toOrderItem(request);
            OrderItemEntity savedOrderItem = this.orderItemRepository.save(orderItem);
            return new MessageResponse(savedOrderItem.getId().toString());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when creating order item: " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }

    /**
     * Converts an OrderItemRequest into an OrderItem entity.
     *
     * @param request the incoming order item data
     * @return the mapped OrderItem entity with the associated order and menu
     *         product
     * @throws PGException if the order/menu product does not exist
     */
    public OrderItemEntity toOrderItem(OrderItemRequest request) throws PGException {
        try {
            OrderItemEntity orderItem = this.orderItemMapper.toEntity(request);

            if (request.getOrderId() != null) {
                Optional<OrderEntity> order = this.orderRepository.findById(request.getOrderId());
                if (order.isPresent()) {
                    orderItem.setOrder(order.get());
                } else {
                    throw new PGException("Order not found with id: " + request.getOrderId());
                }
            }

            if (request.getMenuProductId() != null) {
                Optional<MenuProductEntity> menuProduct = this.menuProductRepository
                        .findById(request.getMenuProductId());
                if (menuProduct.isPresent()) {
                    orderItem.setMenuProduct(menuProduct.get());
                    orderItem.setTotalPrice(menuProduct.get().getPrice() * orderItem.getQuantity());
                } else {
                    throw new PGException("MenuProduct not found with id: " + request.getMenuProductId());
                }
            }
            return orderItem;

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when converting DTO to entity: " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }
}
