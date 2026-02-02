package com.pizzaro_go.menu_product.repository;

import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for MenuProduct entities.
 * Provides custom query methods for accessing menu products.
 */
public interface IMenuProductRepository extends JpaRepository<MenuProductEntity, Long> {

        @Modifying
        @Transactional
        @Query(value = "ALTER TABLE menu_products AUTO_INCREMENT = 1", nativeQuery = true)
        void resetIdSequence();

        /**
         * Finds a menu product by its ID and eagerly fetches its associated stock
         * usages and stock items.
         *
         * @param id the ID of the menu product
         * @return an Optional containing the menu product if found, or empty otherwise
         */
        @Query("SELECT mp FROM MenuProductEntity mp " +
                        "LEFT JOIN FETCH mp.productStockUsages psu " +
                        "LEFT JOIN FETCH psu.stockItem " +
                        "WHERE mp.id = :id")
        Optional<MenuProductEntity> findByIdWithStockUsage(Long id);

        Optional<MenuProductEntity> findFirstByName(String name);
}
