package com.pizzaro_go.product_stock_usage.repository;

import com.pizzaro_go.product_stock_usage.entity.ProductStockUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ProductStockUsage entities.
 */
public interface IProductStockUsageRepository extends JpaRepository<ProductStockUsageEntity, Long> {
    /**
     * Deletes all stock usages associated with a specific menu product.
     *
     * @param menuProductId the ID of the menu product
     */
    void deleteByMenuProductId(Long menuProductId);
}
