package com.openex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // Trading pair, e.g., BTCUSD
    @Column(nullable = false, length = 16)
    private String symbol;

    // BUY or SELL
    @Column(nullable = false, length = 4)
    private String side;

    // Price with high precision for crypto trading
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    // Quantity with high precision
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    // Order status: NEW, FILLED, CANCELLED, etc.
    @Column(nullable = false, length = 16)
    private String status;

    // Timestamp for order creation
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}