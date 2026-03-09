package com.openex.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String side; // BUY or SELL

    private BigDecimal price;

    private BigDecimal quantity;

    private String status;

}