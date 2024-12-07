package com.chacha.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와의 관계 매핑

    @Embedded
    private OrderOption orderOption; // 주문 옵션

    @Builder
    public Order(String orderNumber, LocalDateTime orderDate, User user, OrderOption orderOption) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.user = user;
        this.orderOption = orderOption;
    }
}