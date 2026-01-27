package com.pizzaro_go.menu_product.entity;

import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.oreder_item.entity.OrderItem;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "menu_products")
public class MenuProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "image_URL", length = 200)
    private String imageURL;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @Column(nullable = false)
    private Double price = 0.0;

    @OneToMany(mappedBy = "menuProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductStockUsage> productStockUsages = new ArrayList<>();

    @OneToMany(mappedBy = "menuProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

}
