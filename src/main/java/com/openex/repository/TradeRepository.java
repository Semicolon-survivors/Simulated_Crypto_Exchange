package com.openex.repository;

import com.openex.common.Currency;
import com.openex.trading.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository port for trades persistence and querying.
 */
public interface TradeRepository {

    /**
     * Immutable representation of a stored trade.
     */
    record TradeRecord(
            UUID tradeId,
            UUID orderId,
            Currency baseCurrency,
            Currency quoteCurrency,
            OrderSide side,
            BigDecimal price,
            BigDecimal quantity,
            Instant timestamp
    ) {
        public TradeRecord {
            if (tradeId == null) throw new IllegalArgumentException("tradeId is required");
            if (orderId == null) throw new IllegalArgumentException("orderId is required");
            if (baseCurrency == null || quoteCurrency == null) {
                throw new IllegalArgumentException("baseCurrency and quoteCurrency are required");
            }
            if (side == null) throw new IllegalArgumentException("side is required");
            if (price == null || quantity == null) {
                throw new IllegalArgumentException("price and quantity are required");
            }
            if (timestamp == null) throw new IllegalArgumentException("timestamp is required");
        }

        public String symbol() {
            return baseCurrency + "/" + quoteCurrency;
        }

        public BigDecimal quoteAmount() {
            return price.multiply(quantity);
        }
    }

    /**
     * Persist a trade.
     */
    void save(TradeRecord trade);

    /**
     * Fetch most recent trades for a given symbol.
     */
    List<TradeRecord> findRecentBySymbol(Currency baseCurrency, Currency quoteCurrency, int limit);

    /**
     * Fetch all trades associated with a given order.
     */
    List<TradeRecord> findByOrderId(UUID orderId);
}
