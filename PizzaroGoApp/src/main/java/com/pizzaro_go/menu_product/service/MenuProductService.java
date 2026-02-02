package com.pizzaro_go.menu_product.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import com.pizzaro_go.menu_product.mapper.IMenuProductMapper;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsageEntity;
import com.pizzaro_go.product_stock_usage.repository.IProductStockUsageRepository;
import com.pizzaro_go.product_stock_usage.service.ProductStockUsageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.ArrayList;

import com.pizzaro_go.fileimport.excel.entities.MenuProductFileData;
import com.pizzaro_go.stock_item.entity.StockItemEntity;
import com.pizzaro_go.stock_item.repository.IStockItemRepository;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service layer for menu product operations.
 */
@Service
@Transactional
public class MenuProductService {
    @Autowired
    private IMenuProductRepository menuProductRepository;

    @Autowired
    private IProductStockUsageRepository productStockUsageRepository;

    @Autowired
    private IMenuProductMapper menuProductMapper;

    @Autowired
    private ProductStockUsageService productStockUsageService;

    @Autowired
    private IStockItemRepository stockItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(MenuProductService.class);

    /**
     * Retrieves all menu products.
     *
     * @return a list of MenuProductResponse objects representing the menu products
     * @throws PGException if a repository error occurs during retrieval
     */
    public List<MenuProductResponse> getAll() throws PGException {
        this.log.info("Retrieve all menu products");
        try {
            return this.menuProductMapper.toResponseList(this.menuProductRepository.findAll());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve all menu products ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates an existing menu product.
     *
     * @param menuProductRequest the request containing updated menu product details
     * @return a MessageResponse with the updated menu product ID
     * @throws PGException if a repository error occurs during update
     */
    public MessageResponse update(MenuProductRequest menuProductRequest) throws PGException {
        Long menuProductId = menuProductRequest.getId();
        this.log.info("Updating the menu_product with id: {}", menuProductId);
        try {
            MenuProductEntity menuProductToUpdate = this.menuProductMapper.toEntity(menuProductRequest);
            menuProductToUpdate = this.menuProductRepository.save(menuProductToUpdate);
            setProductStockUsageOnProduct(menuProductToUpdate, menuProductRequest.getStockUsages());

            // Flush to ensure ProductStockUsage records are persisted
            this.entityManager.flush();
            this.entityManager.clear();

            Optional<MenuProductEntity> menuProductOptional = this.menuProductRepository
                    .findByIdWithStockUsage(menuProductId);
            if (menuProductOptional.isPresent()) {
                menuProductToUpdate = menuProductOptional.get();
            } else {
                throw new PGException("MenuProduct not found with id: " + menuProductId);
            }

            String menuProductDescription = getMenuProductDescription(menuProductToUpdate);

            menuProductToUpdate.setDescription(menuProductDescription);

            MenuProductEntity updatedMenuProduct = this.menuProductRepository.save(menuProductToUpdate);
            return new MessageResponse(updatedMenuProduct.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating the menu product  with id: " + menuProductId + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Creates a new menu product.
     *
     * @param menuProductRequest the request containing new menu product details
     * @return a MessageResponse with the new menu product ID
     * @throws PGException if a repository error occurs during save
     */
    public MessageResponse create(MenuProductRequest menuProductRequest) throws PGException {
        this.log.info("Creating a new menu product item: {}", menuProductRequest.getName());
        try {
            MenuProductEntity menuProduct = this.menuProductMapper.toEntity(menuProductRequest);
            menuProduct = this.menuProductRepository.save(menuProduct);

            setProductStockUsageOnProduct(menuProduct, menuProductRequest.getStockUsages());

            // Flush to ensure ProductStockUsage records are persisted
            this.entityManager.flush();
            this.entityManager.clear();

            Long menuProductId = menuProduct.getId();
            Optional<MenuProductEntity> menuProductOptional = this.menuProductRepository
                    .findByIdWithStockUsage(menuProductId);
            if (menuProductOptional.isPresent()) {
                menuProduct = menuProductOptional.get();
            } else {
                throw new PGException("MenuProduct not found with id: " + menuProductId);
            }

            String menuProductDescription = getMenuProductDescription(menuProduct);

            menuProduct.setDescription(menuProductDescription);

            MenuProductEntity savedMenuProduct = this.menuProductRepository.save(menuProduct);
            return new MessageResponse(savedMenuProduct.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when creating new menu product -> " + e.getMessage();
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes a menu product by its ID.
     *
     * @param id the ID of the menu product
     * @return a MessageResponse confirming deletion
     * @throws PGException if the menu product is not found or a repository error
     *                     occurs
     */
    public MessageResponse deleteById(Long id) throws PGException {
        this.log.info("Delete the menu product with id: {}", id);
        try {
            if (!this.menuProductRepository.existsById(id)) {
                throw new PGException("MenuProduct with id " + id + " not found");
            }
            this.menuProductRepository.deleteById(id);
            return new MessageResponse("Menu product successfully deleted!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting the menu product with id: " + id + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes all menu products.
     *
     * @return a MessageResponse confirming all menu products were deleted
     * @throws PGException if a repository error occurs
     */
    public MessageResponse deleteAll() throws PGException {
        this.log.info("Deleting entire menu and resetting IDs.");
        try {
            this.menuProductRepository.deleteAll();
            this.menuProductRepository.resetIdSequence();
            return new MessageResponse("Entire menu successfully deleted and IDs reset!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting entire menu ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Procedurally saves stock usage ingredients for a menu product.
     * This method first deletes any existing stock usages for the product
     * and then creates new ones using the ProductStockUsageService.
     *
     * @param menuProduct        the menu product to save ingredients for
     * @param stockUsageRequests the list of stock usage requests containing stock
     *                           item IDs and quantities
     * @throws PGException if a referenced stock item is not found or a repository
     *                     error occurs
     */
    private void setProductStockUsageOnProduct(MenuProductEntity menuProduct,
            List<ProductStockUsageRequest> stockUsageRequests)
            throws PGException {
        this.log.info("Set stock usages for product with id: " + menuProduct.getId());
        try {

            this.productStockUsageRepository.deleteByMenuProductId(menuProduct.getId());

            if (stockUsageRequests != null && !stockUsageRequests.isEmpty()) {
                for (ProductStockUsageRequest usageRequest : stockUsageRequests) {

                    usageRequest.setMenuProductId(menuProduct.getId());

                    usageRequest.setId(null);
                    this.productStockUsageService.create(usageRequest);
                }
            }
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when setting stock usages for product with id: " + menuProduct.getId();
            this.log.error(errorMsg, e);

            errorMsg += " -> " + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Generates a descriptive string for the menu product based on its stock
     * usages.
     * For products in the PIZZA category, this description lists the names of all
     * used stock items (ingredients).
     *
     * @param menuProduct the menu product for which to generate the description
     * @return a comma-separated string of stock item names, or an empty string if
     *         not applicable
     */
    private String getMenuProductDescription(MenuProductEntity menuProduct) {
        StringJoiner joiner = new StringJoiner(", ");
        if (menuProduct.getProductStockUsages() != null) {
            for (ProductStockUsageEntity usage : menuProduct.getProductStockUsages()) {
                if (usage.getStockItem() != null) {
                    String stockItemName = usage.getStockItem().getName();
                    String unit = StringUtils.capitalize(usage.getStockItem().getUnit().toString());
                    double quantity = usage.getQuantityPerUnit();
                    String ingredient = stockItemName + " " + quantity + " " + unit;
                    joiner.add(ingredient);
                } else {
                    this.log.warn("ProductStockUsage {} has null stockItem", usage.getId());
                }
            }
        }
        String description = joiner.toString();
        this.log.info("Final description: '{}'", description);
        return description;
    }

    /**
     * Generates an Excel file containing all menu products.
     *
     * @return a byte array containing the Excel file data
     * @throws PGException if an error occurs during export
     */
    public byte[] exportMenuProducts() throws PGException {
        this.log.info("Generating Excel export for menu products.");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Menu Products");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = { "Name", "Category", "Price", "Description", "Image URL" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            List<MenuProductEntity> products = this.menuProductRepository.findAll();
            int rowIdx = 1;
            for (MenuProductEntity product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getName());
                row.createCell(1)
                        .setCellValue(product.getProductCategory() != null ? product.getProductCategory().name() : "");
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getDescription());
                row.createCell(4).setCellValue(product.getImageURL());
            }

            workbook.write(outputStream);
            this.log.info("Successfully generated menu products Excel file.");
            return outputStream.toByteArray();

        } catch (IOException | RepositoryException e) {
            String errorMsg = "Error occurred while generating menu products Excel file -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }

    /**
     * Imports menu products from an Excel file.
     *
     * @param file the Excel file containing menu product data
     * @throws PGException if an error occurs during processing
     */
    public void importMenuProducts(MultipartFile file) throws PGException {
        this.log.info("Clearing existing menu products and importing from Excel file.");
        try (InputStream stream = file.getInputStream()) {
            // Clear existing menu products before import
            this.menuProductRepository.deleteAll();
            this.menuProductRepository.resetIdSequence();

            List<MenuProductFileData> menuProductFileDataList = Poiji.fromExcel(stream, PoijiExcelType.XLSX,
                    MenuProductFileData.class);

            for (MenuProductFileData fileData : menuProductFileDataList) {
                MenuProductRequest request = new MenuProductRequest();
                request.setName(fileData.getName());
                request.setProductCategory(fileData.getCategory());
                request.setPrice(fileData.getPrice());
                request.setImageURL(fileData.getImageURL());

                // Parse description for ingredients
                List<ProductStockUsageRequest> usageRequests = new ArrayList<>();
                String description = fileData.getDescription();
                if (description != null && !description.isEmpty()) {
                    String[] ingredients = description.split(",\\s*");
                    for (String ingredientStr : ingredients) {
                        ingredientStr = ingredientStr.trim();
                        if (ingredientStr.isEmpty())
                            continue;

                        String[] parts = ingredientStr.split("\\s+");
                        if (parts.length >= 3) {
                            // Format: [Name parts...] [Quantity] [Unit]
                            // We know: last is Unit, second-to-last is Quantity
                            int quantityIndex = parts.length - 2;

                            StringBuilder stockItemNameBuilder = new StringBuilder();
                            for (int i = 0; i < quantityIndex; i++) {
                                if (i > 0)
                                    stockItemNameBuilder.append(" ");
                                stockItemNameBuilder.append(parts[i]);
                            }
                            String stockItemName = stockItemNameBuilder.toString().trim();

                            try {
                                double quantity = Double.parseDouble(parts[quantityIndex]);

                                Optional<StockItemEntity> stockItemOpt = this.stockItemRepository
                                        .findFirstByNameIgnoreCase(stockItemName);
                                if (stockItemOpt.isPresent()) {
                                    ProductStockUsageRequest usageRequest = new ProductStockUsageRequest();
                                    usageRequest.setStockItemId(stockItemOpt.get().getId());
                                    usageRequest.setQuantityPerUnit(quantity);
                                    usageRequests.add(usageRequest);
                                } else {
                                    this.log.warn("Stock item not found by name: {}", stockItemName);
                                }
                            } catch (NumberFormatException e) {
                                this.log.warn("Could not parse quantity for ingredient: {}", ingredientStr);
                            }
                        }
                    }
                }
                request.setStockUsages(usageRequests);

                // Since we cleared the table, we just create new entries
                this.create(request);
            }
        } catch (IOException e) {
            String errorMsg = "Error processing Excel file -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        } catch (NumberFormatException e) {
            String errorMsg = "Error parsing quantity in description -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }
}
