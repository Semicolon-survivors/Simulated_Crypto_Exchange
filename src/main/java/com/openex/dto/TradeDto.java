package com.openex.dto;

import com.openex.common.Currency;
import com.openex.trading.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Trade DTO representing an executed trade.
 */
public record TradeDto(
        UUID tradeId,
        UUID orderId,
        Currency baseCurrency,
        Currency quoteCurrency,
        OrderSide side,
        BigDecimal price,
        BigDecimal quantity,
        Instant timestamp
) {
    public TradeDto {
        if (tradeId == null) {
            throw new IllegalArgumentException("tradeId is required");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (baseCurrency == null || quoteCurrency == null) {
            throw new IllegalArgumentException("baseCurrency and quoteCurrency are required");
        }
        if (side == null) {
            throw new IllegalArgumentException("side is required");
        }
        if (price == null || quantity == null) {
            throw new IllegalArgumentException("price and quantity are required");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    /**
     * Convenience: symbol in BASE/QUOTE format.
     */
    public String symbol() {
        return baseCurrency + "/" + quoteCurrency;
    }

    /**
     * Quote amount = price * quantity.
     */
    public BigDecimal quoteAmount() {
        return price.multiply(quantity);
    }
}
