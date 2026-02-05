package com.pizzaro_go.menu_product.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.service.MenuProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for menu product operations.
 */
@RestController
@RequestMapping("/api/menu_products")
public class MenuProductController {
    @Autowired
    private MenuProductService menuProductService;

    /**
     * Retrieves all menu products.
     *
     * @return a list of MenuProductResponse objects representing the menu products
     */
    @GetMapping("")
    @Operation(summary = "Retrieve all menu products")
    public ResponseEntity<List<MenuProductResponse>> getAll() {
        return ResponseEntity.ok(this.menuProductService.getAll());
    }

    /**
     * Creates a new menu product.
     *
     * @param menuProductRequest the request containing new menu product details
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("")
    @Operation(summary = "Create a new menu product")
    public ResponseEntity<MessageResponse> create(@RequestBody MenuProductRequest menuProductRequest) {
        MessageResponse response = this.menuProductService.create(menuProductRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing menu product.
     *
     * @param menuProductRequest the request containing updated menu product details
     * @return a ResponseEntity with MessageResponse
     */

    @PatchMapping("")
    @Operation(summary = "Update an menu product")
    public ResponseEntity<MessageResponse> update(@RequestBody MenuProductRequest menuProductRequest) {
        MessageResponse response = this.menuProductService.update(menuProductRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a menu product by its ID.
     *
     * @param id the ID of the menu product to delete
     * @return a ResponseEntity with MessageResponse
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an menuProduct by id")
    public ResponseEntity<MessageResponse> deleteById(@PathVariable("id") Long id) {
        MessageResponse response = this.menuProductService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes all menu products.
     *
     * @return a ResponseEntity with MessageResponse
     */
    @DeleteMapping("")
    @Operation(summary = "Delete entire menu")
    public ResponseEntity<MessageResponse> deleteAll() {
        MessageResponse response = this.menuProductService.deleteAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Exports all menu products to an Excel file and returns it for download.
     *
     * @return a ResponseEntity containing the Excel file bytes
     */
    @GetMapping("/export")
    @Operation(summary = "Exports menu products to an Excel file for download")
    public ResponseEntity<byte[]> exportMenuProducts() {
        byte[] excelContent = this.menuProductService.exportMenuProducts();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=menu_products.xlsx")
                .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_OCTET_STREAM))
                .body(excelContent);
    }

    /**
     * Imports menu products from an Excel file.
     *
     * @param file the Excel file containing menu product data
     * @return a ResponseEntity with MessageResponse
     */
    @PostMapping("/import")
    @Operation(summary = "Imports menu products from an Excel file")
    public ResponseEntity<MessageResponse> importMenuProducts(@RequestParam("file") MultipartFile file) {
        this.menuProductService.importMenuProducts(file);
        return ResponseEntity.ok(new MessageResponse("Menu products imported successfully!"));
    }
}
