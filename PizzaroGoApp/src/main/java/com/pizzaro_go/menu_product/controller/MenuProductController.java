package com.pizzaro_go.menu_product.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.service.MenuProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pizzaro_go.common.exceptions.PGException;

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
    public List<MenuProductResponse> getAll() {
        return this.menuProductService.getAll();
    }

    /**
     * Creates a new menu product.
     *
     * @param menuProductRequest the request containing new menu product details
     * @return a MessageResponse with the new menu product ID
     */
    @PostMapping("")
    @Operation(summary = "Create a new menu product")
    public MessageResponse create(@RequestBody MenuProductRequest menuProductRequest) {
        return this.menuProductService.create(menuProductRequest);
    }

    /**
     * Updates an existing menu product.
     *
     * @param menuProductRequest the request containing updated menu product details
     * @return a MessageResponse with the updated menu product ID
     */

    @PatchMapping("")
    @Operation(summary = "Update an menu product")
    public MessageResponse update(@RequestBody MenuProductRequest menuProductRequest) {
        return this.menuProductService.update(menuProductRequest);
    }

    /**
     * Deletes a menu product by its ID.
     *
     * @param id the ID of the menu product to delete
     * @return a MessageResponse confirming deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an menuProduct by id")
    public MessageResponse deleteById(@PathVariable("id") Long id) {
        return this.menuProductService.deleteById(id);
    }

    /**
     * Deletes all menu products.
     *
     * @return a MessageResponse confirming all menu products were deleted
     */
    @DeleteMapping("")
    @Operation(summary = "Delete entire menu")
    public MessageResponse deleteAll() {
        return this.menuProductService.deleteAll();
    }

    /**
     * Exports all menu products to an Excel file and returns it for download.
     *
     * @return a ResponseEntity containing the Excel file bytes
     */
    @GetMapping("/export")
    @Operation(summary = "Exports menu products to an Excel file for download")
    public ResponseEntity<byte[]> exportMenuProducts() {
        try {
            byte[] excelContent = this.menuProductService.exportMenuProducts();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=menu_products.xlsx")
                    .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_OCTET_STREAM))
                    .body(excelContent);
        } catch (PGException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Imports menu products from an Excel file.
     *
     * @param file the Excel file containing menu product data
     * @return a MessageResponse confirming the import
     */
    @PostMapping("/import")
    @Operation(summary = "Imports menu products from an Excel file")
    public MessageResponse importMenuProducts(@RequestParam("file") MultipartFile file) {
        this.menuProductService.importMenuProducts(file);
        return new MessageResponse("Menu products imported successfully!");
    }
}
