package com.openex.trading;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderRequest {
    private UUID userId;
    private OrderType type;
    private OrderSide side;

    // For LIMIT and SELL MARKET
    private BigDecimal quantity;      // BTC amount for SELL or target BTC for BUY LIMIT
    private BigDecimal limitPrice;    // USD for LIMIT

    // For BUY MARKET
    private BigDecimal quoteAmount;   // USD amount for BUY MARKET

    public UUID getUserId() {
        return userId;
    }

    public OrderRequest setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public OrderType getType() {
        return type;
    }

    public OrderRequest setType(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderRequest setSide(OrderSide side) {
        this.side = side;
        return this;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public OrderRequest setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public OrderRequest setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
        return this;
    }

    public BigDecimal getQuoteAmount() {
        return quoteAmount;
    }

    public OrderRequest setQuoteAmount(BigDecimal quoteAmount) {
        this.quoteAmount = quoteAmount;
        return this;
    }
}
