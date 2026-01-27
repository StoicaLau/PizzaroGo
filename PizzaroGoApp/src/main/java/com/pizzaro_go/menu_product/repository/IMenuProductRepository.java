package com.pizzaro_go.menu_product.repository;

import com.pizzaro_go.menu_product.entity.MenuProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IMenuProductRepository extends JpaRepository<MenuProduct, Long> {

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
    @Query("SELECT mp FROM MenuProduct mp " +
            "LEFT JOIN FETCH mp.productStockUsages psu " +
            "LEFT JOIN FETCH psu.stockItem " +
            "WHERE mp.id = :id")
    Optional<MenuProduct> findByIdWithStockUsage(Long id);

    /**
     * Finds all menu products and eagerly fetches their associated stock usages and
     * stock items.
     *
     * @return a list of all menu products with their stock usages and stock items
     *         fetched
     */
    @Query("SELECT DISTINCT mp FROM MenuProduct mp " +
            "LEFT JOIN FETCH mp.productStockUsages psu " +
            "LEFT JOIN FETCH psu.stockItem")
    List<MenuProduct> findAllWithStockUsage();
}
