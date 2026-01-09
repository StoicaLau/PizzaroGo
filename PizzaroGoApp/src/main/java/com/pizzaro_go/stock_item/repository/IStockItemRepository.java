package com.pizzaro_go.stock_item.repository;

import com.pizzaro_go.stock_item.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for StockItem entities.
 *
 */
public interface IStockItemRepository extends JpaRepository<StockItem, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE stock_item RESTART IDENTITY", nativeQuery = true)
    void truncateTable();
}
