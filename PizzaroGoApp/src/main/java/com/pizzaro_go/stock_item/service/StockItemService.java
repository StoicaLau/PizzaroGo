package com.pizzaro_go.stock_item.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.enums.Category;
import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.fileimport.excel.entities.StockItemFileData;
import com.pizzaro_go.stock_item.dtos.StockItemRequest;
import com.pizzaro_go.stock_item.dtos.StockItemResponse;
import com.pizzaro_go.stock_item.entity.StockItemEntity;
import com.pizzaro_go.stock_item.mapper.IStockItemMapper;
import com.pizzaro_go.stock_item.repository.IStockItemRepository;
import org.springframework.transaction.annotation.Transactional;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//TODO verificat daca sunt in stock sau nu daca e la limita sau daca e mai put4in , aici putem face la get all products
//TODO sa pui ResponseEntity
//TODO sesiune la autentificare
//TODO CORS
/**
 * Service layer for stockItem operations.
 */
@Service
@Transactional
public class StockItemService {

    @Autowired
    private IStockItemRepository stockItemRepository;

    @Autowired
    private IStockItemMapper stockItemMapper;

    private final Logger log = LoggerFactory.getLogger(StockItemService.class);

    /**
     * Retrieves the entire stock.
     *
     * @return a list of StockResponse objects representing the stock items
     * @throws PGException if a repository error occurs during retrieval
     */
    public List<StockItemResponse> getAll() throws PGException {
        this.log.info("Retrieve entire stock.");
        try {
            return this.stockItemMapper.toResponseList(this.stockItemRepository.findAll());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve entire stock ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Retrieves stock items filtered by product category.
     * If the product category is PIZZA, it returns items with category INGREDIENT.
     * Otherwise, it returns items with category PRODUCT.
     *
     * @param productCategoryStr the product category as a string
     * @return a list of StockItemResponse objects
     * @throws PGException if a repository error occurs
     */
    public List<StockItemResponse> getByProductCategory(String productCategoryStr) throws PGException {
        this.log.info("Retrieve stock items for product category: {}", productCategoryStr);
        try {
            ProductCategory productCategory = ProductCategory.valueOf(productCategoryStr.toUpperCase());
            Category categoryToFilter;

            if (productCategory == ProductCategory.PIZZA) {
                categoryToFilter = Category.INGREDIENT;
            } else {
                categoryToFilter = Category.PRODUCT;
            }

            List<StockItemEntity> entities = this.stockItemRepository.findByCategory(categoryToFilter);
            return this.stockItemMapper.toResponseList(entities);
        } catch (IllegalArgumentException e) {
            throw new PGException("Invalid product category: " + productCategoryStr);
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve stock items by category ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates an existing stock item.
     *
     * @param stockItem the request containing updated stock item details
     * @return a MessageResponse with the updated stock item ID
     * @throws PGException if a repository error occurs during update
     */
    public MessageResponse update(StockItemRequest stockItem) throws PGException {
        Long stockItemId = stockItem.getId();
        this.log.info("Updating the stock_item with id: {}", stockItemId);
        try {
            StockItemEntity stockItemToUpdate = this.stockItemMapper.toEntity(stockItem);
            StockItemEntity updatedStockItem = this.stockItemRepository.save(stockItemToUpdate);
            return new MessageResponse(updatedStockItem.getId().toString());

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating the stock item with id: " + stockItemId + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes a stock item by its ID.
     *
     * @param id the ID of the stock item
     * @return a MessageResponse confirming deletion
     * @throws PGException if the stock item is not found or a repository error
     *                     occurs
     */
    public MessageResponse deleteById(Long id) throws PGException {
        this.log.info("Delete the stock item with id: {}", id);
        try {
            if (!this.stockItemRepository.existsById(id)) {
                throw new PGException("StockItem with id " + id + " not found");
            }
            this.stockItemRepository.deleteById(id);
            return new MessageResponse("Stock item successfully deleted!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting the stock item with id: " + id + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes all stock items.
     *
     * @return a MessageResponse confirming all stock items were deleted
     * @throws PGException if a repository error occurs
     */
    public MessageResponse deleteAll() throws PGException {
        this.log.info("Deleting entire stock and resetting IDs.");
        try {
            this.stockItemRepository.deleteAll();
            this.stockItemRepository.resetIdSequence();
            return new MessageResponse("Entire Stock successfully deleted and IDs reset!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting entire stock  ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Adds a new stock item.
     *
     * @param stockItemRequest the request containing new stock item details
     * @return a MessageResponse with the new stock item ID
     * @throws PGException if a repository error occurs during save
     */
    public MessageResponse create(StockItemRequest stockItemRequest) throws PGException {
        this.log.info("Creating a new stock item: {}", stockItemRequest.getName());
        try {
            StockItemEntity stockItem = this.stockItemMapper.toEntity(stockItemRequest);

            StockItemEntity savedStockItem = this.stockItemRepository.save(stockItem);
            return new MessageResponse(savedStockItem.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when creating new stock item -> " + e.getMessage();
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Imports stocks from an Excel file.
     *
     * @param file the Excel file containing stock data
     * @throws PGException if an error occurs during processing
     */
    public void importStockItems(MultipartFile file) throws PGException {
        this.log.info("Clearing existing stocks and importing from Excel file.");
        try (InputStream stream = file.getInputStream()) {
            // Clear existing stocks before import
            this.stockItemRepository.deleteAll();
            this.stockItemRepository.resetIdSequence();

            List<StockItemFileData> stockFileDataList = Poiji.fromExcel(stream, PoijiExcelType.XLSX,
                    StockItemFileData.class);

            List<StockItemEntity> stocks = stockFileDataList.stream()
                    .map(this.stockItemMapper::toEntity)
                    .toList();

            this.stockItemRepository.saveAll(stocks);
        } catch (IOException e) {
            String errorMsg = "Error processing Excel file -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        } catch (RepositoryException e) {
            String errorMsg = "Error saving imported stocks -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }

    /**
     * Generates an Excel file containing all stock items.
     *
     * @return a byte array containing the Excel file data
     * @throws PGException if an error occurs during export
     */
    public byte[] exportStockItems() throws PGException {
        this.log.info("Generating Excel export for stocks.");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Stocks");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = { "Name", "Category", "Quantity", "Unit" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            List<StockItemEntity> stocks = this.stockItemRepository.findAll();
            int rowIdx = 1;
            for (StockItemEntity stock : stocks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(stock.getName());
                row.createCell(1).setCellValue(stock.getCategory() != null ? stock.getCategory().name() : "");
                row.createCell(2).setCellValue(stock.getQuantity());
                row.createCell(3).setCellValue(stock.getUnit() != null ? stock.getUnit().name() : "");
            }

            workbook.write(outputStream);
            this.log.info("Successfully generated stocks Excel file.");
            return outputStream.toByteArray();

        } catch (IOException | RepositoryException e) {
            String errorMsg = "Error occurred while generating stocks Excel file -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }
}
