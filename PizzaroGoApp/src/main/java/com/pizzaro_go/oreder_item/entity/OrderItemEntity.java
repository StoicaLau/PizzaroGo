package com.pizzaro_go.oreder_item.entity;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import com.pizzaro_go.order.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_product"))
    private MenuProductEntity menuProduct;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
}
