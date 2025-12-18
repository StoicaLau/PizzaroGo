package com.pizzaro_go.order.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.entity.Order;
import com.pizzaro_go.order.repository.IOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for order operations.
 *
 */
@Service
public class OrderService {

    private final IOrderRepository orderRepository;
    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * Creates a new OrderService with the given repository.
     *
     * @param orderRepository the repository used for order persistence
     */
    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order.
     *
     * @param order the request containing order details
     * @return a MessageResponse with the created order ID
     * @throws PGException if a repository error occurs during creation
     */
    public MessageResponse create(OrderRequest order) throws PGException {
        try {
            String username = order.getUser().getUsername();
            this.log.info("Create an order for: {}", username);
            Order orderToSave = this.orderRepository.save(order.toOrder());
            return new MessageResponse(orderToSave.getId().toString());
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
    public OrderResponse getById(String id) throws PGException {
        try {
            this.log.info("Retrieving an order");
            Integer idToFind = Integer.parseInt(id);
            Optional<Order> order = this.orderRepository.findById(idToFind);
            if (order.isPresent()) {
                return new OrderResponse(order.get());
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
        String orderId = order.getId();
        this.log.info("Updating the order with id: {}", orderId);
        try {
            Order orderToUpdate = this.orderRepository.save(order.toOrder());
            return new MessageResponse(orderToUpdate.getId().toString());

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
    public MessageResponse deleteById(String id) throws PGException {
        this.log.info("Delete the order with id: {}", id);
        try {
            Integer orderId = Integer.parseInt(id);

            if (!orderRepository.existsById(orderId)) {
                throw new PGException("Order with id " + id + " not found!");
            }

            orderRepository.deleteById(orderId);
            return new MessageResponse("Order successfully deleted!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting the order with id: " + id + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }
}
