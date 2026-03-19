package com.openex.repository;

import com.openex.common.Currency;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for wallet persistence.
 * Provides simple methods to load and store wallet balances per user and currency.
 */
public interface WalletRepository {

    /**
     * Immutable snapshot of a wallet balance.
     */
    record WalletSnapshot(
            UUID userId,
            Currency currency,
            BigDecimal available,
            BigDecimal locked
    ) {
        public WalletSnapshot {
            if (userId == null) throw new IllegalArgumentException("userId is required");
            if (currency == null) throw new IllegalArgumentException("currency is required");
            if (available == null || locked == null) {
                throw new IllegalArgumentException("available and locked are required");
            }
        }

        public BigDecimal total() {
            return available.add(locked);
        }
    }

    /**
     * Find a wallet by user and currency.
     */
    Optional<WalletSnapshot> findByUserIdAndCurrency(UUID userId, Currency currency);

    /**
     * Save or update the provided snapshot.
     */
    WalletSnapshot save(WalletSnapshot snapshot);

    /**
     * Update balances for a wallet atomically.
     */
    void updateBalances(UUID userId, Currency currency, BigDecimal available, BigDecimal locked);
}
