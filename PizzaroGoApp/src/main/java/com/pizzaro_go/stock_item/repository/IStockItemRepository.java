package com.pizzaro_go.stock_item.repository;

import com.pizzaro_go.stock_item.entity.StockItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StockItem entities.
 *
 */
public interface IStockItemRepository extends JpaRepository<StockItemEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE stock_items AUTO_INCREMENT = 1", nativeQuery = true)
    void resetIdSequence();

    List<StockItemEntity> findByCategory(com.pizzaro_go.common.enums.Category category);

    Optional<StockItemEntity> findFirstByName(String name);

    Optional<StockItemEntity> findFirstByNameIgnoreCase(String name);
}
