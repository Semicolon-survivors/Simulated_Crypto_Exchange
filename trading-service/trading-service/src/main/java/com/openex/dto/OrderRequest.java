package com.openex.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Long userId;
    private String side; // BUY or SELL
    private BigDecimal price;
    private BigDecimal quantity;
}
