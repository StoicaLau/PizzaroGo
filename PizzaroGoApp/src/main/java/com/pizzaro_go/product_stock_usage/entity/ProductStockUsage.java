package com.pizzaro_go.product_stock_usage.entity;


import com.pizzaro_go.menu_product.entity.MenuProduct;
import com.pizzaro_go.stock_item.entity.StockItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock_item")
public class ProductStockUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_stock_usage_product"))
    private MenuProduct menuProduct;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_stock_usage_stock_item"))
    private StockItem stockItem;

    @Column(name = "quantity_per_unit", nullable = false)
    private Double quantityPerUnit = 1.0;

}
