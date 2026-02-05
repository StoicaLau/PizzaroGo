package com.pizzaro_go.product_stock_usage.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.service.ProductStockUsageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for product stock usage operations.
 */
@RestController
@RequestMapping("/api/product_stock_usages")
public class ProductStockUsageController {
    @Autowired
    private ProductStockUsageService productStockUsageService;

    /**
     * Creates a new product stock usage.
     *
     * @param productStockUsageRequest the request containing product stock usage
     *                                 details
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("")
    @Operation(summary = "Create a new product stock usage")
    public ResponseEntity<MessageResponse> create(@RequestBody ProductStockUsageRequest productStockUsageRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productStockUsageService.create(productStockUsageRequest));
    }
}
