package com.pizzaro_go.product_stock_usage.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsageEntity;
import com.pizzaro_go.product_stock_usage.mapper.IProductStockUsageMapper;
import com.pizzaro_go.product_stock_usage.repository.IProductStockUsageRepository;
import com.pizzaro_go.stock_item.entity.StockItemEntity;
import com.pizzaro_go.stock_item.repository.IStockItemRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for product stock usage operations.
 */
@Service
@Transactional
public class ProductStockUsageService {

    @Autowired
    private IProductStockUsageRepository productStockUsageRepository;
    @Autowired
    private IMenuProductRepository menuProductRepository;
    @Autowired
    private IStockItemRepository stockItemRepository;
    @Autowired
    private IProductStockUsageMapper productStockUsageMapper;

    private final Logger log = LoggerFactory.getLogger(ProductStockUsageService.class);

    /**
     * Creates a new product stock usage.
     *
     * @param productStockUsageRequest the request containing product stock usage
     *                                 details
     * @return a MessageResponse with the created product stock usage ID
     * @throws PGException if a repository error occurs during creation
     */
    public MessageResponse create(ProductStockUsageRequest productStockUsageRequest) throws PGException {
        try {
            this.log.info("Create a product stock usage");
            ProductStockUsageEntity productStockUsageToSave = this.toProductStockUsage((productStockUsageRequest));
            productStockUsageToSave = this.productStockUsageRepository.save(productStockUsageToSave);
            return new MessageResponse(productStockUsageToSave.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when trying to create an product stock usage";
            this.log.error(errorMsg, e);

            errorMsg += "-> " + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Converts a ProductStockUsageRequest into a ProductStockUsage entity.
     *
     * @param productStockUsageRequest the incoming product stock usage data
     * @return the mapped ProductStockUsage entity with the associated menu product
     *         and stock item
     * @throws PGException if the menu product/stock item does not exist or a
     *                     repository error occurs
     */
    public ProductStockUsageEntity toProductStockUsage(ProductStockUsageRequest productStockUsageRequest)
            throws PGException {
        try {
            ProductStockUsageEntity productStockUsage = this.productStockUsageMapper.toEntity(productStockUsageRequest);

            Long menuProductId = productStockUsageRequest.getMenuProductId();
            if (menuProductId != null) {
                Optional<MenuProductEntity> menuProduct = this.menuProductRepository.findById(menuProductId);
                if (menuProduct.isPresent()) {
                    productStockUsage.setMenuProduct(menuProduct.get());
                } else {
                    String errorMsg = "Cannot convert ProductStockUsage DTO to entity because no MenuProduct was found with id: "
                            + menuProductId + " -> ";
                    log.error(errorMsg);
                    throw new PGException(errorMsg);
                }
            }

            Long stockItemId = productStockUsageRequest.getStockItemId();
            if (stockItemId != null) {
                Optional<StockItemEntity> stockItem = this.stockItemRepository.findById(stockItemId);
                if (stockItem.isPresent()) {
                    productStockUsage.setStockItem(stockItem.get());
                } else {
                    String errorMsg = "Cannot convert ProductStockUsage DTO to entity because no StockItem was found with id: "
                            + stockItemId + " -> ";
                    log.error(errorMsg);
                    throw new PGException(errorMsg);
                }
            }

            return productStockUsage;

        } catch (RepositoryException e) {

            String errorMsg = "ProductStockUsage conversion from DTO to entity failed due to an error";
            this.log.error(errorMsg, e);
            errorMsg += "->" + e.getMessage();
            throw new PGException(errorMsg);
        }
    }

}
