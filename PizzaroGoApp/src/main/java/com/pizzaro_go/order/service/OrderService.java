package com.pizzaro_go.order.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.entity.OrderEntity;
import com.pizzaro_go.order.mapper.IOrderMapper;
import com.pizzaro_go.order.repository.IOrderRepository;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.entity.OrderItemEntity;
import com.pizzaro_go.oreder_item.repository.IOrderItemRepository;
import com.pizzaro_go.oreder_item.service.OrderItemService;
import com.pizzaro_go.user.entity.UserEntity;
import com.pizzaro_go.user.repository.IUserRepository;
import com.pizzaro_go.stock_item.repository.IStockItemRepository;
import com.pizzaro_go.stock_item.entity.StockItemEntity;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsageEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for order operations.
 *
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderItemRepository orderItemRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private IOrderMapper orderMapper;

    @Autowired
    private IStockItemRepository stockItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * Creates a new order.
     *
     * @param order the request containing order details
     * @return a MessageResponse with the created order ID
     * @throws PGException if a repository error occurs during creation
     */
    public MessageResponse create(OrderRequest order) throws PGException {
        try {
            this.log.info("Create an order for the user with id: {}", order.getUserId());
            OrderEntity orderToSave = this.toOrder(order);
            orderToSave.setStatus(Status.PENDING);
            orderToSave.setCreatedAt(LocalDateTime.now());

            orderToSave = this.orderRepository.save(orderToSave);
            setOrderItemToOrder(orderToSave, order.getOrderItems());

            this.entityManager.flush();
            this.entityManager.clear();

            orderToSave = this.orderRepository.findByIdWithOrderItems(orderToSave.getId())
                    .orElseThrow(() -> new PGException("Order not found after initial save"));

            calculateOrderPrices(orderToSave);
            updateStock(orderToSave, true);

            OrderEntity savedOrder = this.orderRepository.save(orderToSave);

            return new MessageResponse(savedOrder.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to create an order";
            this.log.error(errorMsg, e);

            errorMsg += "-> " + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order as a string
     * @return an OrderResponse with order details
     * @throws PGException if the order is not found or a repository error occurs
     */
    public OrderResponse getById(Long id) throws PGException {
        try {
            this.log.info("Retrieving an order");
            Optional<OrderEntity> order = this.orderRepository.findById(id);
            if (order.isPresent()) {
                return this.orderMapper.toResponse(order.get());
            } else {
                String errorMsg = "Could not find any order with the provided id:" + id + "-> ";
                log.error(errorMsg);
                throw new PGException(errorMsg);
            }

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to get the order by id " + id;
            this.log.error(errorMsg, e);

            errorMsg += "->" + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates an existing order.
     *
     * @param order the request containing updated order details
     * @return a MessageResponse with the updated order ID
     * @throws PGException if a repository error occurs during update
     */
    public MessageResponse update(OrderRequest order) throws PGException {
        Long orderId = order.getId();
        this.log.info("Updating the order with id: {}", orderId);
        try {
            OrderEntity existingOrder = this.orderRepository.findByIdWithOrderItems(orderId)
                    .orElse(null);

            if (existingOrder != null && existingOrder.getStatus() != Status.CANCELED) {
                updateStock(existingOrder, false);
            }

            OrderEntity orderToUpdate = this.toOrder(order);
            orderToUpdate = this.orderRepository.save(orderToUpdate);

            setOrderItemToOrder(orderToUpdate, order.getOrderItems());

            this.entityManager.flush();
            this.entityManager.clear();

            orderToUpdate = this.orderRepository.findByIdWithOrderItems(orderId)
                    .orElseThrow(() -> new PGException("Order not found with id: " + orderId));

            calculateOrderPrices(orderToUpdate);

            if (orderToUpdate.getStatus() != Status.CANCELED) {
                updateStock(orderToUpdate, true);
            }

            OrderEntity updatedOrder = this.orderRepository.save(orderToUpdate);

            return new MessageResponse(updatedOrder.getId().toString());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating the order with id: " + orderId + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order as a string
     * @return a MessageResponse confirming deletion
     * @throws PGException if the order is not found or a repository error occurs
     */
    public MessageResponse deleteById(Long id) throws PGException {
        this.log.info("Delete the order with id: {}", id);
        try {

            if (!this.orderRepository.existsById(id)) {
                throw new PGException("Order with id " + id + " not found!");
            }

            this.orderRepository.deleteById(id);
            return new MessageResponse("Order successfully deleted!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting the order with id: " + id + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of OrderResponse objects representing the user's orders
     * @throws PGException if the user does not exist or if a repository error
     *                     occurs
     */
    public List<OrderResponse> getAllByUserId(Long userId) throws PGException {
        this.log.info("Retrieve all orders by user id: {} ", userId);
        try {
            if (!this.userRepository.existsById(userId)) {
                throw new PGException("Cannot retrieve orders because the user with id " + userId + " does not exist.");
            }

            return this.orderRepository.getAllByUserId(userId).stream().map(this.orderMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve all orders by user id: " + userId + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }

    }

    /**
     * Retrieves all active orders (PENDING or PROCESSING).
     *
     * @return a list of OrderResponse objects representing active orders
     * @throws PGException if a repository error occurs
     */
    public List<OrderResponse> getActiveOrders() throws PGException {
        this.log.info("Retrieving all active orders (PENDING, PROCESSING)");
        try {
            List<Status> activeStatuses = List.of(Status.PENDING, Status.PROCESSING);
            List<OrderEntity> orders = this.orderRepository.findAllByStatusIn(activeStatuses);
            return this.orderMapper.toResponseList(orders);
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieving active orders -> ";
            this.log.error(errorMsg, e);
            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates the status of an existing order and its associated order items.
     * <p>
     * When an order status is updated to PROCESSING, the delivery time is set and
     * the status of order items is updated: PIZZA items become PROCESSING,
     * while others (SAUCES, DRINKS) become READY.
     * When an order is CANCELED or DELIVERED, all its items inherit that status.
     *
     * @param orderRequest the request containing the order ID, new status, and
     *                     estimated delivery time
     * @return an OrderResponse with the updated order details
     * @throws PGException if the order is not found
     */
    public OrderResponse updateOrderStatus(OrderRequest orderRequest) {
        Long orderId = orderRequest.getId();
        this.log.info("Updating status for order #{} to {}", orderId, orderRequest.getStatus());

        try {
            OrderEntity order = this.orderRepository.findByIdWithOrderItems(orderId)
                    .orElseThrow(() -> new PGException("Order not found with id: " + orderId));

            Status oldStatus = order.getStatus();
            Status newStatus = Status.valueOf(orderRequest.getStatus().toUpperCase());
            order.setStatus(newStatus);

            if (newStatus == Status.CANCELED && oldStatus != Status.CANCELED) {
                updateStock(order, false);
            } else if (newStatus != Status.CANCELED && oldStatus == Status.CANCELED) {
                updateStock(order, true);
            }

            if (orderRequest.getEstimatedAt() != null) {
                order.setEstimatedAt(orderRequest.getEstimatedAt());
                this.log.debug("Setting estimated delivery time for order #{} to {}", orderId,
                        orderRequest.getEstimatedAt());
            }

            // Logic for updating individual order items based on the new order status
            updateOrderItemStatuses(order, newStatus);

            OrderEntity savedOrder = this.orderRepository.save(order);
            this.log.info("Order #{} status successfully updated to {}", orderId, newStatus);

            return this.orderMapper.toResponse(savedOrder);

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating status for order with id: " + orderId + " -> ";
            this.log.error(errorMsg, e);
            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Helper method to update order item statuses based on the new order status.
     */
    private void updateOrderItemStatuses(OrderEntity order, Status newOrderStatus) {
        for (OrderItemEntity orderItem : order.getOrderItems()) {
            if (newOrderStatus == Status.PROCESSING) {
                // For processing orders, pizzas need prep time, other items are ready
                // immediately
                if (orderItem.getMenuProduct().getProductCategory() == ProductCategory.PIZZA) {
                    orderItem.setStatus(Status.PROCESSING);
                } else {
                    orderItem.setStatus(Status.READY);
                }
            } else if (newOrderStatus == Status.CANCELED || newOrderStatus == Status.DELIVERED) {
                // Final states are propagated to all items
                orderItem.setStatus(newOrderStatus);
            } else {
                // For other states (READY, PENDING), items follow the order status
                orderItem.setStatus(newOrderStatus);
            }
        }
    }

    /**
     * Converts an OrderRequest into an Order entity .
     *
     * @param orderRequest the incoming order data
     * @return the mapped Order entity with the associated user
     * @throws PGException if the user does not exist or a repository error occurs
     */
    private OrderEntity toOrder(OrderRequest orderRequest) throws PGException {
        try {
            OrderEntity order = this.orderMapper.toEntity(orderRequest);
            Long userId = orderRequest.getUserId();

            Optional<UserEntity> user = this.userRepository.findById(userId);
            if (user.isPresent()) {
                order.setUser(user.get());
                return order;
            } else {
                String errorMsg = "Cannot convert order DTO to entity because no user was found with id: " + userId
                        + "-> ";
                log.error(errorMsg);
                throw new PGException(errorMsg);
            }
        } catch (RepositoryException e) {
            String errorMsg = "Order conversion from DTO to entity failed due to a repository error";
            this.log.error(errorMsg, e);

            errorMsg += "->" + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    private void setOrderItemToOrder(OrderEntity order, List<OrderItemRequest> orderItemRequestList)
            throws PGException {
        try {
            this.orderItemRepository.deleteByOrderId(order.getId());
            if (orderItemRequestList != null && !orderItemRequestList.isEmpty()) {
                for (OrderItemRequest orderItemRequest : orderItemRequestList) {
                    orderItemRequest.setOrderId(order.getId());

                    orderItemRequest.setId(null);
                    this.orderItemService.create(orderItemRequest);
                }
            }

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when setting order items for order with id: " + order.getId();
            this.log.error(errorMsg, e);

            errorMsg += " -> " + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Calculates the order prices based on the associated order items.
     *
     * @param order the order for which to calculate prices
     */
    private void calculateOrderPrices(OrderEntity order) {
        double orderPrice = 0.0;
        if (order.getOrderItems() != null) {
            for (OrderItemEntity item : order.getOrderItems()) {
                if (item.getTotalPrice() != null) {
                    orderPrice += item.getTotalPrice();
                }
            }
        }
        order.setOrderPrice(orderPrice);
        order.setTotalPrice(orderPrice + (order.getDeliveryPrice() != null ? order.getDeliveryPrice() : 0.0));
    }

    /**
     * Updates the stock level based on the items in an order.
     *
     * @param order    the order containing items
     * @param subtract true to subtract from stock, false to add back
     */
    private void updateStock(OrderEntity order, boolean subtract) {
        if (order.getOrderItems() == null)
            return;

        for (OrderItemEntity item : order.getOrderItems()) {
            if (item.getMenuProduct() == null || item.getMenuProduct().getProductStockUsages() == null) {
                continue;
            }

            for (ProductStockUsageEntity usage : item.getMenuProduct().getProductStockUsages()) {
                StockItemEntity stockItem = usage.getStockItem();
                if (stockItem != null) {
                    double amount = item.getQuantity() * usage.getQuantityPerUnit();
                    if (subtract) {
                        stockItem.setQuantity(stockItem.getQuantity() - amount);
                        this.log.info("Subtracting {} from stock item {} (New: {})", amount, stockItem.getName(),
                                stockItem.getQuantity());
                    } else {
                        stockItem.setQuantity(stockItem.getQuantity() + amount);
                        this.log.info("Restoring {} to stock item {} (New: {})", amount, stockItem.getName(),
                                stockItem.getQuantity());
                    }
                    this.stockItemRepository.save(stockItem);
                }
            }
        }
    }

}
