package com.pizzaro_go.order.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order operations.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Creates a new order.
     *
     * @param orderRequest the request containing order details
     * @return a MessageResponse with the created order ID
     */
    @PostMapping("")
    @Operation(summary = "Create a new order")
    public MessageResponse create(@RequestBody OrderRequest orderRequest) {
        return this.orderService.create(orderRequest);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order
     * @return an OrderResponse with order details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Create an order by id")
    public OrderResponse getById(@PathVariable("id") Long id) {
        return this.orderService.getById(id);
    }

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of OrderResponse objects representing the user's orders
     */
    @GetMapping("/byUserId/{userId}")
    @Operation(summary = "Retrieves all orders belonging to a specific user")
    public List<OrderResponse> getAllByUserId(@PathVariable("userId") Long userId) {
        return this.orderService.getAllByUserId(userId);
    }

    /**
     * Updates an existing order.
     *
     * @param orderRequest the request containing updated order details
     * @return a MessageResponse with the updated order ID
     */
    @PatchMapping("")
    @Operation(summary = "Update an order")
    public MessageResponse update(@RequestBody OrderRequest orderRequest) {
        return this.orderService.update(orderRequest);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order
     * @return a MessageResponse confirming deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order by id")
    public MessageResponse deleteById(@PathVariable("id") Long id) {
        return this.orderService.deleteById(id);
    }

}
