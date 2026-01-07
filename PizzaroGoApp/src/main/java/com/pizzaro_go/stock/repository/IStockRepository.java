package com.pizzaro_go.stock.repository;


import com.pizzaro_go.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Stock entities.
 *
 */
public interface IStockRepository extends JpaRepository<Stock, Long> {
}
