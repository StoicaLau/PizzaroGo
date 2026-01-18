package com.pizzaro_go.stock_item.entity;

import com.pizzaro_go.common.enums.Category;
import com.pizzaro_go.common.enums.Unit;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The stock entity
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock_item")
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double quantity = 0.0;

    @Enumerated(EnumType.STRING)
    @Column()
    private Unit unit;

    @OneToMany(mappedBy = "stockItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductStockUsage> usages = new ArrayList<>();

}
