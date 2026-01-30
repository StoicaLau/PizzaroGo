package com.pizzaro_go.oreder_item.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.dtos.OrderItemResponse;
import com.pizzaro_go.oreder_item.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @return an OrderItemResponse with the updated order item details
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order item status")
    public OrderItemResponse updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return this.orderItemService.updateStatus(id, status);
    }

    /**
     * Creates a new order item.
     *
     * @param request the request containing order item details
     * @return a MessageResponse with the created order item ID
     */
    @PostMapping("")
    @Operation(summary = "Create a new order item")
    public ResponseEntity<MessageResponse> create(@RequestBody OrderItemRequest request) {
        try {
            return ResponseEntity.ok(this.orderItemService.create(request));
        } catch (PGException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
