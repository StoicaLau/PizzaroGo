package com.pizzaro_go.order.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("")
    @Operation(summary = "Create a new order")
    public ResponseEntity<MessageResponse> create(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.create(orderRequest));
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order
     * @return a ResponseEntity with OrderResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Create an order by id")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.orderService.getById(id));
    }

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a ResponseEntity with a list of OrderResponse objects
     */
    @GetMapping("/byUserId/{userId}")
    @Operation(summary = "Retrieves all orders belonging to a specific user")
    public ResponseEntity<List<OrderResponse>> getAllByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(this.orderService.getAllByUserId(userId));
    }

    /**
     * Updates an existing order.
     *
     * @param orderRequest the request containing updated order details
     * @return a ResponseEntity with MessageResponse
     */
    @PatchMapping("")
    @Operation(summary = "Update an order")
    public ResponseEntity<MessageResponse> update(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(this.orderService.update(orderRequest));
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order
     * @return a ResponseEntity with MessageResponse
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order by id")
    public ResponseEntity<MessageResponse> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.orderService.deleteById(id));
    }

    /**
     * Retrieves all active orders (PENDING or PROCESSING).
     *
     * @return a ResponseEntity with a list of OrderResponse objects
     */
    @GetMapping("/active")
    @Operation(summary = "Retrieve all active orders mapping to PENDING and PROCESSING statuses")
    public ResponseEntity<List<OrderResponse>> getActiveOrders() {
        return ResponseEntity.ok(this.orderService.getActiveOrders());
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderRequest the request containing the order ID and the new status
     * @return a ResponseEntity with OrderResponse
     */
    @PatchMapping("/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponse> updateStatus(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(this.orderService.updateOrderStatus(orderRequest));
    }

}
