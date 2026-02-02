package com.pizzaro_go.stock_item.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.stock_item.dtos.StockItemRequest;
import com.pizzaro_go.stock_item.dtos.StockItemResponse;
import com.pizzaro_go.stock_item.service.StockItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * REST controller for stock operations.
 */
@RestController
@RequestMapping("/api/stock_items")
public class StockItemController {
    @Autowired
    private StockItemService stockItemService;

    /**
     * Retrieves the entire stock.
     *
     * @return a list of StockResponse objects representing the stock items
     */
    @GetMapping("")
    @Operation(summary = "Retrieves entire stock")
    public List<StockItemResponse> getAll() {
        return this.stockItemService.getAll();
    }

    /**
     * Retrieves stock items filtered by product category.
     *
     * @param productCategory the product category to filter by
     * @return a list of StockItemResponse objects
     */
    @GetMapping("/by-product-category/{productCategory}")
    @Operation(summary = "Retrieves stock items by product category")
    public List<StockItemResponse> getByProductCategory(@PathVariable("productCategory") String productCategory) {
        return this.stockItemService.getByProductCategory(productCategory);
    }

    /**
     * Adds a new stock item.
     *
     * @param stockItemRequest the request containing new stock item details
     * @return a MessageResponse with the new stock item ID
     */
    @PostMapping("")
    @Operation(summary = "Add a new stock item")
    public MessageResponse create(@RequestBody StockItemRequest stockItemRequest) {
        return this.stockItemService.create(stockItemRequest);
    }

    /**
     * Updates an existing stock item.
     *
     * @param stockItemRequest the request containing updated stock item details
     * @return a MessageResponse with the updated stock item ID
     */
    @PatchMapping("")
    @Operation(summary = "Update an stock item")
    public MessageResponse update(@RequestBody StockItemRequest stockItemRequest) {
        return this.stockItemService.update(stockItemRequest);
    }

    /**
     * Deletes a stock item by its ID.
     *
     * @param id the ID of the stock item to delete
     * @return a MessageResponse confirming deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an stockItem by id")
    public MessageResponse deleteById(@PathVariable("id") Long id) {
        return this.stockItemService.deleteById(id);
    }

    /**
     * Deletes all stock items.
     *
     * @return a MessageResponse confirming all stock items were deleted
     */
    @DeleteMapping("")
    @Operation(summary = "Delete entire stock")
    public MessageResponse deleteAll() {
        return this.stockItemService.deleteAll();
    }

    /**
     * Imports stocks from an Excel file.
     *
     * @param file the Excel file uploaded by the user
     * @return a success message wrapped in MessageResponse
     */
    @PostMapping("/import")
    @Operation(summary = "Imports stocks from Excel file")
    public ResponseEntity<MessageResponse> importStockItems(@RequestParam("file") MultipartFile file) {
        try {
            this.stockItemService.importStockItems(file);
            return ResponseEntity.ok(new MessageResponse("Stocks imported successfully."));
        } catch (PGException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Exports all stock items to an Excel file and returns it for download.
     *
     * @return a ResponseEntity containing the Excel file bytes
     */
    @GetMapping("/export")
    @Operation(summary = "Exports stocks to an Excel file for download")
    public ResponseEntity<byte[]> exportStockItems() {
        try {
            byte[] excelContent = this.stockItemService.exportStockItems();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock_items.xlsx")
                    .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_OCTET_STREAM))
                    .body(excelContent);
        } catch (PGException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
