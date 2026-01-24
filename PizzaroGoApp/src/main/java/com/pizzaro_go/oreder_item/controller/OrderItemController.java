package com.pizzaro_go.oreder_item.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for order item operations.
 */
@RestController
@RequestMapping("/api/order_items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

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
