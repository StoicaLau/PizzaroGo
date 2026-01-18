package com.pizzaro_go.product_stock_usage.repository;

import com.pizzaro_go.product_stock_usage.entity.ProductStockUsage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ProductStockUsage entities.
 */
public interface IProductStockUsageRepository extends JpaRepository<ProductStockUsage, Long> {
}
