package com.pizzaro_go.product_stock_usage.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.service.ProductStockUsageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for product stock usage operations.
 */
@RestController
@RequestMapping("/productStockUsages")
public class ProductStockUsageController {

    private final ProductStockUsageService productStockUsageService;

    /**
     * Creates a new ProductStockUsageController with the given service.
     *
     * @param productStockUsageService the service handling product stock usage
     *                                 logic
     */
    public ProductStockUsageController(ProductStockUsageService productStockUsageService) {
        this.productStockUsageService = productStockUsageService;
    }

    /**
     * Creates a new product stock usage.
     *
     * @param productStockUsageRequest the request containing product stock usage
     *                                 details
     * @return a MessageResponse with the created product stock usage ID
     */
    @PostMapping("")
    @Operation(summary = "Create a new product stock usage")
    public MessageResponse create(@RequestBody ProductStockUsageRequest productStockUsageRequest) {
        return this.productStockUsageService.create(productStockUsageRequest);
    }
}
