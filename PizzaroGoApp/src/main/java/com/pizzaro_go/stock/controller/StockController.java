package com.pizzaro_go.stock.controller;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.stock.dtos.StockResponse;
import com.pizzaro_go.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for stock operations.
 */
@RestController
@RequestMapping("/stocks")
public class StockController {
    private final StockService stockService;

    /**
     * Creates a new StockController with the given service.
     *
     * @param stockService the service handling stock logic
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Retrieves the entire stock.
     *
     * @return a list of StockResponse objects representing the stock items
     */
    @GetMapping("")
    @Operation(summary = "Retrieves entire stock")
    public List<StockResponse> getAll() {
        return this.stockService.getAll();
    }

    /**
     * Imports stocks from an Excel file.
     *
     * @param file the Excel file uploaded by the user
     * @return a success message wrapped in MessageResponse
     */
    @PostMapping("/import")
    @Operation(summary = "Imports stocks from Excel file")
    public ResponseEntity<MessageResponse> importStocks(@RequestParam("file") MultipartFile file) {
        try {
            this.stockService.importStocks(file);
            return ResponseEntity.ok(new MessageResponse("Stocks imported successfully."));
        } catch (PGException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
