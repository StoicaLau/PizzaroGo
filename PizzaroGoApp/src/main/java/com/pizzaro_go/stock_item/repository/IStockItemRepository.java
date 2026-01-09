package com.pizzaro_go.stock_item.repository;


import com.pizzaro_go.stock_item.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for StockItem entities.
 *
 */
public interface IStockItemRepository extends JpaRepository<StockItem, Long> {
}
