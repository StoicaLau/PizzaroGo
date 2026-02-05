package com.pizzaro_go.oreder_item.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.dtos.OrderItemResponse;
import com.pizzaro_go.oreder_item.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order item operations.
 */
@RestController
@RequestMapping("/api/order_items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * Updates the status of an existing order item.
     *
     * @param id     the ID of the order item
     * @param status the new status
     * @return a ResponseEntity with OrderItemResponse
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order item status")
    public ResponseEntity<OrderItemResponse> updateStatus(@PathVariable("id") Long id,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(this.orderItemService.updateStatus(id, status));
    }

    /**
     * Creates a new order item.
     *
     * @param request the request containing order item details
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("")
    @Operation(summary = "Create a new order item")
    public ResponseEntity<MessageResponse> create(@RequestBody OrderItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderItemService.create(request));
    }
}
