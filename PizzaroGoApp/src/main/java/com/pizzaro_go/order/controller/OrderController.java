package com.pizzaro_go.order.controller;


import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order operations.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new OrderController with the given service.
     *
     * @param orderService the service handling order logic
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

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
    public OrderResponse getById(@PathParam("id") String id) {
        return this.orderService.getById(id);
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
    @Operation
    public MessageResponse delete(@PathParam("id") String id) {
        return this.orderService.deleteById(id);
    }

}
