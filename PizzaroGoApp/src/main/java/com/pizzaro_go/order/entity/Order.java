package com.pizzaro_go.order.entity;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_user"))
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "estimated_at")
    private LocalDateTime estimatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "order_price", nullable = false, precision = 10, scale = 2)
    private Double orderPrice = 0.0;

    @Column(name = "delivery_price", nullable = false, precision = 10, scale = 2)
    private Double deliveryPrice = 0.0;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private Double totalPrice = 0.0;
}
