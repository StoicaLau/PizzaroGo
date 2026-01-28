package com.pizzaro_go.menu_product.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.entity.MenuProduct;
import com.pizzaro_go.menu_product.mapper.IMenuProductMapper;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsage;
import com.pizzaro_go.product_stock_usage.repository.IProductStockUsageRepository;
import com.pizzaro_go.product_stock_usage.service.ProductStockUsageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

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
            MenuProduct menuProductToUpdate = this.menuProductMapper.toEntity(menuProductRequest);
            menuProductToUpdate = this.menuProductRepository.save(menuProductToUpdate);
            setProductStockUsageOnProduct(menuProductToUpdate, menuProductRequest.getStockUsages());

            // Flush to ensure ProductStockUsage records are persisted
            this.entityManager.flush();
            this.entityManager.clear();

            menuProductToUpdate = this.menuProductRepository.findByIdWithStockUsage(menuProductId)
                    .orElseThrow(() -> new PGException("MenuProduct not found with id: " + menuProductId));

            String menuProductDescription = getMenuProductDescription(menuProductToUpdate);

            menuProductToUpdate.setDescription(menuProductDescription);

            MenuProduct updatedMenuProduct = this.menuProductRepository.save(menuProductToUpdate);
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
            MenuProduct menuProduct = this.menuProductMapper.toEntity(menuProductRequest);
            menuProduct = this.menuProductRepository.save(menuProduct);

            setProductStockUsageOnProduct(menuProduct, menuProductRequest.getStockUsages());

            // Flush to ensure ProductStockUsage records are persisted
            this.entityManager.flush();
            this.entityManager.clear();

            menuProduct = this.menuProductRepository.findByIdWithStockUsage(menuProduct.getId())
                    .orElseThrow(() -> new PGException("MenuProduct not found after initial save"));



            String menuProductDescription = getMenuProductDescription(menuProduct);

            menuProduct.setDescription(menuProductDescription);

            MenuProduct savedMenuProduct = this.menuProductRepository.save(menuProduct);
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
    private void setProductStockUsageOnProduct(MenuProduct menuProduct,
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
    private String getMenuProductDescription(MenuProduct menuProduct) {
        String menuProductDescription = "";

        if (menuProduct.getProductCategory().equals(ProductCategory.PIZZA)
                && menuProduct.getProductStockUsages() != null) {
            this.log.info("Product is PIZZA with {} stock usages", menuProduct.getProductStockUsages().size());
            StringJoiner joiner = new StringJoiner(", ");
            for (ProductStockUsage usage : menuProduct.getProductStockUsages()) {
                if (usage.getStockItem() != null) {
                    String stockItemName = usage.getStockItem().getName();
                    String unit = StringUtils.capitalize(usage.getStockItem().getUnit().toString());
                    double quantity = usage.getQuantityPerUnit();
                    String ingredient = stockItemName + " " + quantity + unit;
                    joiner.add(ingredient);
                } else {
                    this.log.warn("ProductStockUsage {} has null stockItem", usage.getId());
                }
            }
            menuProductDescription = joiner.toString();
            this.log.info("Final description: '{}'", menuProductDescription);
        } else {
            this.log.info("Not generating description - Category: {}, StockUsages null: {}",
                    menuProduct.getProductCategory(),
                    menuProduct.getProductStockUsages() == null);
        }
        return menuProductDescription;
    }
}
