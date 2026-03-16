package com.openex.trading;

import com.openex.common.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Order {
    public static final String SYMBOL = "BTC-USD";

    private final UUID id;
    private final UUID userId;
    private final OrderType type;
    private final OrderSide side;
    private final BigDecimal quantity;     // in BTC for SELL; for BUY LIMIT it's BTC quantity
    private final BigDecimal limitPrice;   // in USD for LIMIT, null for MARKET
    private final Instant createdAt;

    private volatile OrderStatus status;

    // reservation bookkeeping
    private final Currency reservedCurrency; // USD for BUY, BTC for SELL
    private final BigDecimal reservedAmount;

    // simple fill fields for future extension
    private volatile BigDecimal filledQuantity = BigDecimal.ZERO;
    private volatile BigDecimal executedNotional = BigDecimal.ZERO;

    public Order(UUID id,
                 UUID userId,
                 OrderType type,
                 OrderSide side,
                 BigDecimal quantity,
                 BigDecimal limitPrice,
                 Currency reservedCurrency,
                 BigDecimal reservedAmount) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.type = Objects.requireNonNull(type);
        this.side = Objects.requireNonNull(side);
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.reservedCurrency = reservedCurrency;
        this.reservedAmount = reservedAmount;
        this.createdAt = Instant.now();
        this.status = OrderStatus.OPEN;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public String getSymbol() {
        return SYMBOL;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Currency getReservedCurrency() {
        return reservedCurrency;
    }

    public BigDecimal getReservedAmount() {
        return reservedAmount;
    }

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public BigDecimal getExecutedNotional() {
        return executedNotional;
    }
}
