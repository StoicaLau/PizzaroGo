package com.pizzaro_go.menu_product.repository;

import com.pizzaro_go.menu_product.entity.MenuProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IMenuProductRepository extends JpaRepository<MenuProduct,Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE menu_product RESTART IDENTITY", nativeQuery = true)
    void truncateTable();
}
