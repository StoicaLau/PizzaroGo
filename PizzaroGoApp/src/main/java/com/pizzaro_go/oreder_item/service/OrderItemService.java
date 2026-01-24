package com.pizzaro_go.oreder_item.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.menu_product.entity.MenuProduct;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import com.pizzaro_go.order.entity.Order;
import com.pizzaro_go.order.repository.IOrderRepository;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.entity.OrderItem;
import com.pizzaro_go.oreder_item.mapper.IOrderItemMapper;
import com.pizzaro_go.oreder_item.repository.IOrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for order item operations.
 */
@Service
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
            OrderItem orderItem = this.toOrderItem(request);
            OrderItem savedOrderItem = this.orderItemRepository.save(orderItem);
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
    public OrderItem toOrderItem(OrderItemRequest request) throws PGException {
        try {
            OrderItem orderItem = this.orderItemMapper.toEntity(request);

            if (request.getOrderId() != null) {
                Optional<Order> order = this.orderRepository.findById(request.getOrderId());
                if (order.isPresent()) {
                    orderItem.setOrder(order.get());
                } else {
                    throw new PGException("Order not found with id: " + request.getOrderId());
                }
            }

            if (request.getMenuProductId() != null) {
                Optional<MenuProduct> menuProduct = this.menuProductRepository.findById(request.getMenuProductId());
                if (menuProduct.isPresent()) {
                    orderItem.setMenuProduct(menuProduct.get());
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
